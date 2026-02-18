@setup
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
    And enriched customer name should be "JOHN"

  @api
  Scenario: Customer API response is persisted and validated
    Given an API endpoint is available for customer status
    When customer status payload is posted for customer "CUST_API_001"
    And api response is saved to API response table
    Then API response should be persisted with status "ACTIVE" and source "MOCK-API"
