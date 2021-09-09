CREATE TABLE Person (
    Id        BIGINT IDENTITY,
    FirstName NVARCHAR(20) NOT NULL,
    LastName  NVARCHAR(20) NOT NULL,
    CreatedAt DATETIME2,
    CONSTRAINT PK_Person PRIMARY KEY (Id)
);

CREATE TABLE PersonSettings (
    Id       BIGINT IDENTITY,
    PersonId BIGINT NOT NULL,
    CONSTRAINT PK_PersonSettings PRIMARY KEY (Id),
    CONSTRAINT FK_PersonSettings_PersonId FOREIGN KEY (PersonId) REFERENCES Person(Id) ON DELETE CASCADE
);

CREATE TABLE NoArgsEntity (
    Id    BIGINT IDENTITY,
    Value NVARCHAR(20),
    CONSTRAINT PK_NoArgsEntity PRIMARY KEY (Id),
);

CREATE TABLE TransientEntity (
    Id    BIGINT IDENTITY,
    Value NVARCHAR(20),
    CONSTRAINT PK_TransientEntity PRIMARY KEY (Id),
);

CREATE TABLE PagingEntity (
    Id    BIGINT IDENTITY,
    Value NVARCHAR(20),
    CONSTRAINT PK_PagingEntity PRIMARY KEY (Id),
);

CREATE TABLE sorting_entity (
   id    BIGINT IDENTITY,
   foo_bar NVARCHAR(20),
   CONSTRAINT PK_sorting_entity PRIMARY KEY (id),
);
