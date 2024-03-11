CREATE TABLE person (
    Id        BIGINT AUTO_INCREMENT,
    FirstName NVARCHAR(20) NOT NULL,
    LastName  NVARCHAR(50) NOT NULL,
    CONSTRAINT PK_Person PRIMARY KEY (Id)
);

CREATE TABLE personsettings (
    Id       BIGINT AUTO_INCREMENT,
    PersonId BIGINT NOT NULL,
    CONSTRAINT PK_PersonSettings PRIMARY KEY (Id),
    CONSTRAINT FK_PersonSettings_PersonId FOREIGN KEY (PersonId) REFERENCES person(Id) ON DELETE CASCADE
);

CREATE TABLE noargsentity (
    Id    BIGINT AUTO_INCREMENT,
    Value NVARCHAR(20),
    CONSTRAINT PK_NoArgsEntity PRIMARY KEY (Id)
);

CREATE TABLE transiententity (
    Id    BIGINT AUTO_INCREMENT,
    Value NVARCHAR(20),
    CONSTRAINT PK_TransientEntity PRIMARY KEY (Id)
);

CREATE TABLE pagingentity (
    Id    BIGINT AUTO_INCREMENT,
    Value NVARCHAR(20),
    CONSTRAINT PK_PagingEntity PRIMARY KEY (Id)
);

CREATE TABLE sorting_entity (
   id    BIGINT AUTO_INCREMENT,
   foo_bar NVARCHAR(20),
   CONSTRAINT PK_sorting_entity PRIMARY KEY (id)
);