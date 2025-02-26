package com.hsbc.balance.service;

public interface IFileService {

    <T> void bakFile(String filename, T message);

    <T> void errFile(String filename, T message);
}
