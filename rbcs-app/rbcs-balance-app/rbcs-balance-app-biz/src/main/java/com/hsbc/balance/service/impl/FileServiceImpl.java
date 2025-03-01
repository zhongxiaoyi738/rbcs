package com.hsbc.balance.service.impl;

import com.hsbc.balance.constant.Filename;
import com.hsbc.balance.service.IFileService;
import com.hsbc.common.utils.FileUtils;
import com.hsbc.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
public class FileServiceImpl implements IFileService {
    @Value("${bakfile.history}")
    private Integer history;

    @Async
    @Override
    public <T> void bakFile(String filename, T message) {
        FileUtils.appendToFile(Path.of(Filename.ROOT_PATH, Filename.BAK_PATH, filename+Filename.SUFFIX), JsonUtils.toJson(message));
        FileUtils.cleanOld(Path.of(Filename.ROOT_PATH, Filename.BAK_PATH), history);
    }

    @Override
    public <T> void errFile(String filename, T message) {
        FileUtils.appendToFile(Path.of(Filename.ROOT_PATH, Filename.ERR_PATH, filename+Filename.SUFFIX), JsonUtils.toJson(message));
    }
}
