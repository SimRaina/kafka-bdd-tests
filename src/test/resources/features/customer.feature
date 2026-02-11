Feature: Kafka customer ingestion

  Scenario Outline: Customer data is consumed and persisted
    Given a customer Avro message is produced
    And the customer message is processed
    Then customer "<customerId>" should exist in database

    Examples:
         | customerId |
         | CUST1      |

    Scenario: Enriched customer data is created from base customer table
      Given valid customer events are processed
      When enrichment job is triggered
      Then enriched customer table should have 1 record
      And enriched customer name should be "John"

