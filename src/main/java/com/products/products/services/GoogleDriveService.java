package com.products.products.services;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Service;
import com.google.api.services.drive.model.Permission;
import java.io.IOException;
import java.util.Collections;

@Service
public class GoogleDriveService {


public String uploadFile(Drive drive, String fileName, byte[] data, String mimeType, String folderId) throws IOException {
    File fileMetadata = new File();
    fileMetadata.setName(fileName);
    if (folderId != null) {
        fileMetadata.setParents(Collections.singletonList(folderId));
    }

    ByteArrayContent content = new ByteArrayContent(mimeType, data);

    // 1️⃣ Upload file
    File uploadedFile = drive.files()
            .create(fileMetadata, content)
            .setFields("id, webViewLink, webContentLink")
            .execute();

    // 2️⃣ Make file publicly readable
    drive.permissions().create(
            uploadedFile.getId(),
            new Permission().setType("anyone").setRole("reader")
    ).execute();

    // 3️⃣ Return a *direct PDF content* URL
    return "https://drive.google.com/uc?export=download&id=" + uploadedFile.getId();
}

    // Optionally create folder if not exists
    public String getOrCreateFolder(Drive drive, String folderName) throws IOException {
        FileList result = drive.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder' and name='" + folderName + "' and trashed=false")
                .setFields("files(id, name)")
                .execute();

        if (result.getFiles().isEmpty()) {
            File folderMetadata = new File();
            folderMetadata.setName(folderName);
            folderMetadata.setMimeType("application/vnd.google-apps.folder");
            File folder = drive.files().create(folderMetadata).setFields("id").execute();
            return folder.getId();
        } else {
            return result.getFiles().get(0).getId();
        }
    }
}



