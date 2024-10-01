Feature: Register of a person in the system

  Scenario: Registration of a valid person
    Given I want to register a new person
    When I provide this following informations
      | firstName | lastName | birthDate                 | gender | birthPlace     | nationality |
      | john      | rambo    | 1965-02-15T02:37:00-07:00 | MALE   | Bowie, Arizona | american    |
    And and step juste for the fun
    Then the person name is present inside the system
    And We have no error message during the creation

#  Scenario: Echec de l'enregistrement d'une personne
#    Given l'utilisateur souhaite créer une nouvelle personne
#    When l'utilisateur fournit le nom "Jean Dupont"
##    And l'utilisateur fournit un email invalide "jean.dupont"
#    And l'utilisateur fournit la date de naissance "01/01/1980"
#    Then une erreur "Email invalide" est renvoyée
#    And la personne n'est pas créée dans le système