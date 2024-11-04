Feature: change gender of a person during the life

  Background:
    Given I have an existing person whose firstname is "Ellen",
    And lastname is "Page",
    And birthdate is "1987-02-21T02:42:00-04:00",
    And birthplace is "Halifax",
    And gender is "Female",
    And nationality is "Canadian"

  Scenario: Ellen want to become a men
    Given I give the current date "2020-08-21T10:04", and the gender "male" and an uuid
    When I engage the changeSex business act
    Then I see the history of gender like bellow in the order
      | gender | changeDate          |
      | female | 1987-02-21T02:42:00 |
      | male   | 2020-08-21T10:04    |
