package com.infobip.spring.data.common;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.full.KClasses;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.core.TypeInformation;
import org.springframework.data.mapping.*;
import org.springframework.data.util.KotlinReflectionUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;

;

/**
 * Utility class to find the preferred constructor which is compatible with both Spring Data JDBC and QueryDSL.
 */
interface PreferredConstructorDiscoverer {

    @Nullable
    static <T, P extends PersistentProperty<P>> PreferredConstructor<T, P> discover(Class<T> type) {
        return Discoverers.findDiscoverer(type)
                .discover(TypeInformation.of(type), null);
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
                            var primaryConstructor = KClasses
                                    .getPrimaryConstructor(JvmClassMappingKt.getKotlinClass(type.getType()));

                            if (primaryConstructor == null) {
                                return DEFAULT.discover(type, entity);
                            }

                            var javaConstructor = ReflectJvmMapping.getJavaConstructor(primaryConstructor);

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

            var parameterTypes = typeInformation.getParameterTypes(constructor);
            var parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(constructor);

            Parameter<Object, P>[] parameters = new Parameter[parameterTypes.size()];
            var parameterAnnotations = constructor.getParameterAnnotations();

            for (var i = 0; i < parameterTypes.size(); i++) {

                var name = parameterNames == null || parameterNames.length <= i ? null : parameterNames[i];
                var type = parameterTypes.get(i);
                var annotations = parameterAnnotations[i];

                parameters[i] = new Parameter(name, type, annotations, entity);
            }

            return new PreferredConstructor<>((Constructor<T>) constructor, parameters);
        }
    }
}
