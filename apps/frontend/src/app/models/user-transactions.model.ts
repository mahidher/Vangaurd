export interface Transaction {
  transaction_id: number;
  transaction_type: 'credit' | 'debit';
  amount: number;
  timestamp: string;
}

export interface UserTransactionData {
  username: string;
  current_balance: number;
  transaction_history: Transaction[];
}


 