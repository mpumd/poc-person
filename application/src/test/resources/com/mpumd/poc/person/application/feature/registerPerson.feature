Feature: Register a person in the system

#  @disabled
  Scenario: Register a valid person
    Given I provide this following informations
      | firstName | lastName | birthDate                 | gender | birthPlace     | nationality |
      | john      | rambo    | 1965-02-15T02:37:00-07:00 | MALE   | Bowie, Arizona | US          |
      | corben    | dallas   | 1960-08-08T14:45:00-07:00 | MALE   | Texas          | TT          |
    When I engage the registration of persons
    Then The persons are present inside the system

  Scenario: Register a person two time
    Given I provide this following informations
      | firstName | lastName | birthDate                 | gender | birthPlace     | nationality |
      | john      | rambo    | 1965-02-15T02:37:00-07:00 | MALE   | Bowie, Arizona | US          |
      | john      | rambo    | 1965-02-15T02:37:00-07:00 | MALE   | Bowie, Arizona | US          |
      | john      | rambo    | 1965-02-15T02:37:00-07:00 | MALE   | Bowie, Arizona | US          |
    When I engage the registration of persons
    Then I receive an already exist person error for "john" "rambo" 2 times

