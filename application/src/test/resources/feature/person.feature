Feature: Register of a person in the system

#  @disabled
  Scenario: Registration of a valid person
    Given I provide this following informations
      | firstName | lastName | birthDate                 | gender | birthPlace     | nationality |
      | john      | rambo    | 1965-02-15T02:37:00-07:00 | MALE   | Bowie, Arizona | US          |
      | corben    | dallas   | 1960-08-08T14:45:00-07:00 | MALE   | Texas          | TT          |
    When I engage the registration of a person
    Then The persons are present inside the system

#  Scenario: Echec de l'enregistrement d'une personne
#    Given l'utilisateur souhaite créer une nouvelle personne
#    When l'utilisateur fournit le nom "Jean Dupont"
##    And l'utilisateur fournit un email invalide "jean.dupont"
#    And l'utilisateur fournit la date de naissance "01/01/1980"
#    Then une erreur "Email invalide" est renvoyée
#    And la personne n'est pas créée dans le système