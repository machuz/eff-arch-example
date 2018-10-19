package example.shared.lib.transactionTask

trait Transaction

trait ReadTransaction extends Transaction

trait ReadWriteTransaction extends ReadTransaction
