package com.bulkrenamer.renamer.service;

import java.util.List;
import java.io.File;

public interface FileRenameService {
    void renameFiles(List<File> files, int renameMethod, String attachment);
}
