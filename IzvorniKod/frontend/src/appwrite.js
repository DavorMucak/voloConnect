import { Client, Account } from 'appwrite';
const client = new Client();
client.setProject('67360315002f28d905a9');
client.setEndpoint('https://cloud.appwrite.io/v1');

const account = new Account(client)
export {account, client};