Feature: change gender of a person during the life

  Background:
    Given I have an existing person whose firstname is "Ellen",
    And lastname is "Page",
    And birthdate is "1987-02-21T02:42:00-04:00",
    And birthplace is "Halifax",
    And gender is "Female",
    And nationality is "Canadian"

  Scenario: Ellen want to become a men, OK
    Given I give the current date "2020-08-21T10:04", and the gender "male" and an uuid
    When I engage the changeSex business act
    Then I see the history of gender like bellow in the order
      | gender | changeDate          |
      | female | 1987-02-21T02:42:00 |
      | male   | 2020-08-21T10:04    |

#  Scenario: Ellen want to become a alien, no possible
#    Given I give the current date "2020-08-21T10:04", and the gender "alien" and an uuid
#    When I engage the changeSex business act
#    Then The system refuse to change the gender with the following message "ellen can't become an alien"

#  Scenario: Ellen want to become a female, but she's already a female
#    Given I give the current date "2020-08-21T10:04", and the gender "female" and an uuid
#    When I engage the changeSex business act
#    Then The system refuse to change the gender with the following message "ellen is already a female"

  Scenario Outline: Ellen want to become a <gender>, KO
    Given I give the current date "<changedate>", and the gender "<gender>" and an uuid
    When I engage the changeSex business act
    Then The system refuse to change the gender with the following message "<message>"

    Examples:
      | changedate       | gender | message                                               |
      | 2020-08-21T10:04 | alien  | Page can't become a Alien. No sugery exist to do that |
      | 2020-08-21T10:04 | female | Page is already a FEMALE                              |