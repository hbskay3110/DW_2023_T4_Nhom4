package io;

import java.io.File;

@Deprecated
public class FileManage {
    private static FileManage instance;
    private boolean fileExists;
    private boolean hasCheckedFile;

    private FileManage() {
        // Khởi tạo trạng thái ban đầu, ví dụ: tệp không tồn tại
        fileExists = false;
        hasCheckedFile = false;
    }

    public static FileManage getInstance() {
        if (instance == null) {
            instance = new FileManage();
        }
        return instance;
    }

    public boolean checkFileExists(File file) {
        if (!hasCheckedFile) {
            // Kiểm tra xem tệp có tồn tại hay không và cập nhật trạng thái
            fileExists = file.exists();
            hasCheckedFile = true;
        }
        return fileExists;
    }

    public boolean getFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }
}
