CREATE TABLE Person (
  Id        BIGINT IDENTITY,
  FirstName NVARCHAR(20) NOT NULL,
  LastName  NVARCHAR(20) NOT NULL,
  CONSTRAINT PK_Person PRIMARY KEY (Id)
);

CREATE TABLE PersonSettings (
  Id       BIGINT IDENTITY,
  PersonId BIGINT NOT NULL,
  CONSTRAINT PK_PersonSettings PRIMARY KEY (Id),
  CONSTRAINT FK_PersonSettings_PersonId FOREIGN KEY (PersonId) REFERENCES Person (Id) ON DELETE CASCADE
);

GO

CREATE PROCEDURE Person_DeleteAndGetFirstNames
    @LastName NVARCHAR(20)
AS
  SET NOCOUNT ON;
  DECLARE @FirstNames TABLE(FirstName NVARCHAR(20));
  DELETE FROM Person
  OUTPUT Deleted.FirstName INTO @FirstNames
  WHERE LastName = @LastName
  SELECT FirstName FROM @FirstNames;

GO

CREATE PROCEDURE Person_Delete
    @LastName NVARCHAR(20)
AS
  SET NOCOUNT ON;
  DECLARE @DeletedPerson TABLE(
    Id        BIGINT,
    FirstName NVARCHAR(20),
    LastName  NVARCHAR(20)
  );
  DELETE FROM Person
  OUTPUT Deleted.Id, Deleted.FirstName, Deleted.LastName INTO @DeletedPerson(Id, FirstName, LastName)
  WHERE LastName = @LastName
  SELECT Id, FirstName, LastName FROM @DeletedPerson;