package com.infobip.spring.data.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KFunction;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mapping.Parameter;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;

;

/**
 * Utility class to find the preferred constructor which is compatible with both Spring Data JDBC and QueryDSL.
 */
interface PreferredConstructorDiscoverer {

    @Nullable
    static <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(Class<T> type) {
        return Discoverers.findDiscoverer(type)
                .discover(ClassTypeInformation.from(type), null);
    }

    enum Discoverers {

        DEFAULT {

            @Nullable
            @Override
            <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(
                    TypeInformation<T> type, @Nullable PersistentEntity<T, P> entity) {

                return Arrays.stream(type.getType().getDeclaredConstructors())
                        .filter(it -> !it.isSynthetic())
                        .filter(it -> it.isAnnotationPresent(PersistenceCreator.class))
                        .map(it -> buildPreferredConstructor(it, type, entity))
                        .findFirst()
                        .orElseGet(() -> Arrays.stream(type.getType().getDeclaredConstructors())
                                .filter(it -> !it.isSynthetic())
                                .max(Comparator.comparingInt(Constructor::getParameterCount))
                                .map(it -> buildPreferredConstructor(it, type, entity))
                                .orElse(null));
            }
        },

        KOTLIN {

            @Nullable
            @Override
            <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(
                    TypeInformation<T> type, @Nullable PersistentEntity<T, P> entity) {

                return Arrays.stream(type.getType().getDeclaredConstructors())
                        .filter(it -> !it.isSynthetic())
                        .filter(it -> it.isAnnotationPresent(PersistenceCreator.class))
                        .map(it -> buildPreferredConstructor(it, type, entity))
                        .findFirst()
                        .orElseGet(() -> {
                            KFunction<T> primaryConstructor = KClasses
                                    .getPrimaryConstructor(JvmClassMappingKt.getKotlinClass(type.getType()));

                            if (primaryConstructor == null) {
                                return DEFAULT.discover(type, entity);
                            }

                            Constructor<T> javaConstructor = ReflectJvmMapping.getJavaConstructor(primaryConstructor);

                            return javaConstructor != null ? buildPreferredConstructor(javaConstructor, type, entity) : null;
                        });
            }
        };

        private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

        private static Discoverers findDiscoverer(Class<?> type) {
            return KotlinReflectionUtils.isSupportedKotlinClass(type) ? KOTLIN : DEFAULT;
        }

        @Nullable
        abstract <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(TypeInformation<T> type,
                                                                                          @Nullable PersistentEntity<T, P> entity);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> buildPreferredConstructor(
                Constructor<?> constructor, TypeInformation<T> typeInformation, @Nullable PersistentEntity<T, P> entity) {

            if (constructor.getParameterCount() == 0) {
                return new PreferredConstructor<>((Constructor<T>) constructor);
            }

            List<TypeInformation<?>> parameterTypes = typeInformation.getParameterTypes(constructor);
            String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(constructor);

            Parameter<Object, P>[] parameters = new Parameter[parameterTypes.size()];
            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();

            for (int i = 0; i < parameterTypes.size(); i++) {

                String name = parameterNames == null || parameterNames.length <= i ? null : parameterNames[i];
                TypeInformation<?> type = parameterTypes.get(i);
                Annotation[] annotations = parameterAnnotations[i];

                parameters[i] = new Parameter(name, type, annotations, entity);
            }

            return new PreferredConstructor<>((Constructor<T>) constructor, parameters);
        }
    }
}
