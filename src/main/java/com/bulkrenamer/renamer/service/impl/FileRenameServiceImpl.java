package com.bulkrenamer.renamer.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bulkrenamer.renamer.service.FileRenameService;

@Service
public class FileRenameServiceImpl implements FileRenameService {
    String getExtension(File fileName){
        int extIndex = fileName.getName().lastIndexOf('.');
        return fileName.getName().substring(extIndex);
    }

    String getFileNamewithoutExt(File fileName){
        int extIndex = fileName.getName().lastIndexOf('.');
        return fileName.getName().substring(0, extIndex);
    }

    @Override
    public void renameFiles(List<File> files, int renameMethod, String attachment){
        // File[] renamedFiles = new File[files.size()];
        switch (renameMethod) {
            case 0:
                for (int i = 0; i < files.size(); i++) {
                    File renamedFile = new File(files.get(i).getParent(), attachment + (i + 1) + getExtension(files.get(i)));
                    files.get(i).renameTo(renamedFile);
                }
                break;

            case 1:
                for (int i = 0; i < files.size(); i++) {
                    File renamedFile = new File(files.get(i).getParent(), getFileNamewithoutExt(files.get(i)) + '_' + attachment + getExtension(files.get(i)));
                    files.get(i).renameTo(renamedFile);
                }
                break;
            case 2:
                for (int i = 0; i < files.size(); i++) {
                    File renamedFile = new File(files.get(i).getParent(), attachment + '_' + getFileNamewithoutExt(files.get(i)) + getExtension(files.get(i)));
                    files.get(i).renameTo(renamedFile);
                }
                break;
            case 3:
                for (int i = 0; i < files.size(); i++) {
                    File renamedFile = new File(files.get(i).getParent(), getFileNamewithoutExt(files.get(i)) + '_' + attachment+ '_'+(i+1)+ getExtension(files.get(i)));
                    files.get(i).renameTo(renamedFile);
                }
                break;
            case 4:
                for (int i = 0; i < files.size(); i++) {
                    File renamedFile = new File(files.get(i).getParent(),(i+1)+""+'_'+attachment + '_'+getFileNamewithoutExt(files.get(i)) + getExtension(files.get(i)));
                    files.get(i).renameTo(renamedFile);
                }
                break;
            case 5:
                SimpleDateFormat ctdt = new SimpleDateFormat("dd-MM-yyyy");
                String dt = ctdt.format(new Date());
                for (int i = 0; i < files.size(); i++) {
                    File renamedFile = new File(files.get(i).getParent(),dt+'_'+attachment + '_'+getFileNamewithoutExt(files.get(i)) + getExtension(files.get(i)));
                    files.get(i).renameTo(renamedFile);
                }
                break;
        }
    }
}
