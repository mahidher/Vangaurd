# Vanguard Backend API Testing Guide

This document contains all the curl commands to test the Vanguard Backend APIs.

## 📋 Prerequisites

- Backend application running on `http://localhost:8080`
- curl installed
- jq installed (optional, for JSON formatting)

## 🏗️ Base URL
```
http://localhost:8080
```

---

## 👥 User Management APIs

### 1. Create Users

#### Create User with Default Balance (0.00)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "alice"
  }'
```

#### Create User with Initial Balance
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "bob",
    "balance": 1500.75
  }'
```

#### Create More Test Users
```bash
# User 3
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "charlie",
    "balance": 5000.00
  }'

# User 4
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "diana",
    "balance": 250.50
  }'
```

#### Test Duplicate Username (Should Fail)
```bash
# Try to create a user with an existing username
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "alice",
    "balance": 500.00
  }'
```
**Expected Result:** This should return an error since "alice" already exists.

### 2. Get All Users
```bash
curl -X GET http://localhost:8080/api/users
```

#### With JSON Formatting (if jq is installed)
```bash
curl -X GET http://localhost:8080/api/users | jq .
```

### 3. Get User by Username
```bash
# Replace USERNAME_HERE with actual username
curl -X GET http://localhost:8080/api/users/USERNAME_HERE
```

#### Example with Sample Username
```bash
curl -X GET http://localhost:8080/api/users/alice
```

### 4. Get User Summary (Balance + Transactions)
```bash
# Get complete user summary including balance and all transactions
curl -X GET http://localhost:8080/api/users/alice/summary
```

#### With JSON Formatting
```bash
curl -X GET http://localhost:8080/api/users/charlie/summary | jq .
```

**Response includes:**
- User balance and creation date
- All transactions in chronological order (newest first)
- Each transaction has a "type" flag: "SENT" or "RECEIVED"
- Transaction totals and counts

### 5. Update User

#### Update Username Only
```bash
curl -X PUT http://localhost:8080/api/users/alice \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "alice_updated"
  }'
```

**⚠️ Important:** When updating a username, ALL related transactions are automatically updated to maintain referential integrity.

#### Update Username and Balance
```bash
curl -X PUT http://localhost:8080/api/users/bob \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "bob_millionaire",
    "balance": 10000.00
  }'
```

### 6. Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/alice
```
---

## 🔧 Admin APIs

### 1. Fraud Detection Analysis
```bash
curl -X GET http://localhost:8080/api/analyse
```

#### With JSON Formatting (if jq is installed)
```bash
curl -X GET http://localhost:8080/api/analyse | jq .
```

**Description:** This endpoint invokes the fraud detection Lambda service to analyze user transactions and detect potential fraudulent activities.

---

## 💸 Transfer & Transaction APIs

### 1. Transfer Funds

#### Basic Transfer
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "bob",
    "toUserName": "bob_millionaire",
    "amount": 250.00,
    "description": "Payment for services"
  }'
```

#### Transfer with Different Amounts
```bash
# Small transfer
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "FROM_USER_NAME_HERE",
    "toUserName": "TO_USER_NAME_HERE",
    "amount": 50.25,
    "description": "Coffee money"
  }'

# Large transfer
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "FROM_USER_NAME_HERE",
    "toUserName": "TO_USER_NAME_HERE",
    "amount": 1000.00,
    "description": "Rent payment"
  }'
```

#### Transfer without Description (optional field)
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "FROM_USER_NAME_HERE",
    "toUserName": "TO_USER_NAME_HERE",
    "amount": 100.00
  }'
```

### 2. Get All Transactions
```bash
curl -X GET http://localhost:8080/api/transfers
```

#### With JSON Formatting
```bash
curl -X GET http://localhost:8080/api/transfers | jq .
```

### 3. Get Transaction by ID
```bash
# Replace TRANSACTION_ID_HERE with actual transaction ID
curl -X GET http://localhost:8080/api/transfers/TRANSACTION_ID_HERE
```

---

## 🧪 Complete Testing Workflow

### Step 1: Create Test Users
```bash
# Create Alice with $1000
ALICE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"userName": "alice", "balance": 1000.00}')

echo "Alice created: $ALICE_RESPONSE"

# Create Bob with $500  
BOB_RESPONSE=$(curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"userName": "bob", "balance": 500.00}')

echo "Bob created: $BOB_RESPONSE"
```

### Step 2: Extract User IDs (manual step)
```bash
# After creating users, copy their IDs from the response
# ALICE_ID="12345678-1234-1234-1234-123456789abc"
# BOB_ID="87654321-4321-4321-4321-210987654321"
```

### Step 3: Verify Initial Balances
```bash
echo "Alice's initial balance:"
curl -X GET http://localhost:8080/api/users/ALICE_ID_HERE

echo "Bob's initial balance:"
curl -X GET http://localhost:8080/api/users/BOB_ID_HERE
```

### Step 4: Perform Transfer
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "alice",
    "toUserName": "bob",
    "amount": 250.00,
    "description": "Test transfer from Alice to Bob"
  }'
```

### Step 5: Verify Updated Balances
```bash
echo "Alice's balance after transfer (should be $750):"
curl -X GET http://localhost:8080/api/users/ALICE_ID_HERE

echo "Bob's balance after transfer (should be $750):"
curl -X GET http://localhost:8080/api/users/BOB_ID_HERE
```

### Step 6: Check Transaction History
```bash
echo "All transactions:"
curl -X GET http://localhost:8080/api/transfers
```

---

## ❌ Error Testing Scenarios

### 1. Insufficient Balance Transfer
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "user_with_low_balance",
    "toUserName": "another_user",
    "amount": 99999.00,
    "description": "This should fail - insufficient balance"
  }'
```

### 2. Transfer to Non-existent User
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "valid_user",
    "toUserName": "non_existent_user",
    "amount": 100.00,
    "description": "This should fail - user not found"
  }'
```

### 3. Transfer from Non-existent User
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "non_existent_user",
    "toUserName": "valid_user",
    "amount": 100.00,
    "description": "This should fail - user not found"
  }'
```

### 4. Self Transfer (Same User)
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "same_user",
    "toUserName": "same_user",
    "amount": 100.00,
    "description": "This should fail - same user"
  }'
```

### 5. Negative Amount Transfer
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "valid_user_1",
    "toUserName": "valid_user_2",
    "amount": -100.00,
    "description": "This should fail - negative amount"
  }'
```

### 6. Zero Amount Transfer
```bash
curl -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" \
  -d '{
    "fromUserName": "valid_user_1",
    "toUserName": "valid_user_2",
    "amount": 0.00,
    "description": "This should fail - zero amount"
  }'
```

### 7. Get Non-existent User
```bash
curl -X GET http://localhost:8080/api/users/non-existent-user-id
```

### 8. Get Non-existent Transaction
```bash
curl -X GET http://localhost:8080/api/transfers/non-existent-transaction-id
```

---

## 📊 Expected Response Formats

### User Response
```json
{
      "userName": "sample_user",
  "userName": "alice",
  "balance": 1000.00,
  "createdAt": "2025-08-04T19:30:45.123456"
}
```

### Transaction Response
```json
{
  "transactionId": "txn-12345678-1234-1234-1234-123456789abc",
      "fromUserName": "user_123",
    "toUserName": "user_456",
  "amount": 250.00,
  "timestamp": "2025-08-04T19:35:20.789012",
  "description": "Payment for services"
}
```

### Error Response
```json
{
  "timestamp": "2025-08-04T19:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Insufficient balance",
  "path": "/api/transfers"
}
```

---

## 🚀 Quick Test Script

Here's a complete bash script to test all functionality:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "🚀 Starting API Tests..."

# Create users
echo "📝 Creating users..."
ALICE=$(curl -s -X POST $BASE_URL/api/users -H "Content-Type: application/json" -d '{"userName": "alice", "balance": 1000.00}')
BOB=$(curl -s -X POST $BASE_URL/api/users -H "Content-Type: application/json" -d '{"userName": "bob", "balance": 500.00}')

echo "Alice: $ALICE"
echo "Bob: $BOB"

# Get all users
echo "👥 Getting all users..."
curl -s -X GET $BASE_URL/api/users | jq .

# Note: You'll need to extract user IDs manually and update the script
# ALICE_ID="extracted-from-above"
# BOB_ID="extracted-from-above"

# Transfer funds
echo "💸 Transferring funds..."
# curl -s -X POST $BASE_URL/api/transfers -H "Content-Type: application/json" \
#   -d "{\"fromUserName\": \"alice\", \"toUserName\": \"bob\", \"amount\": 250.00, \"description\": \"Test transfer\"}" | jq .

# Get transactions
echo "📊 Getting transaction history..."
curl -s -X GET $BASE_URL/api/transfers | jq .

echo "✅ API Tests completed!"
```

---

## 💡 Tips for Testing

1. **Save User IDs**: After creating users, save their IDs for use in transfer operations
2. **Check Logs**: Monitor application logs for detailed transaction information
3. **Use jq**: Install `jq` for better JSON formatting: `brew install jq` (Mac) or `apt-get install jq` (Linux)
4. **Test Error Cases**: Always test both success and failure scenarios
5. **Verify Balances**: Check user balances before and after transfers
6. **Transaction History**: Verify all transactions are recorded correctly

---

## 🔍 Monitoring

Watch application logs in real-time:
```bash
# If running with Maven
tail -f logs/application.log

# Or check console output for detailed logs showing:
# - User creation/updates
# - Transfer validations
# - Balance calculations
# - Transaction records
```

Happy Testing! 🎉 