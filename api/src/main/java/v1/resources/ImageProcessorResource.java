package v1.resources;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("image-processor")
public class ImageProcessorResource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addNewImage(InputStream imageFile) {
        
        /* Upload the image to the GCP Storage and get the url. */
        try {
            String url = gcpUpload(imageFile);
            return Response.ok(url).build();
        } catch (Exception e) {
            return Response.ok(e).build();
        }

        /* Add image entry to the DB. 
        String imageName = new Scanner(request.getPart("image-name").getInputStream())
                .useDelimiter("\\A")
                .next();
        */

        /* Get image id and request image processing. 
        try (
            ResultSet rs2 = stmt.getGeneratedKeys();
        ) {
            if (rs2.next()) {
                int imageId = rs2.getInt(1);
                imageProcessingService.requestProcessing(imageId, url);
            }
        }
        */
        

        
    }

    private String gcpUpload(InputStream imageFile) throws Exception {
        /* Check file by the name. 
        String fileName = filePart.getSubmittedFileName();

        if (fileName == null || fileName.isEmpty() || !fileName.contains(".")) {
            throw new Exception("Invalid file name.");
        }

        String fileNameExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (!(fileNameExtension.equals("jpg") || fileNameExtension.equals("png"))) {
            throw new Exception("File should be either jpg or png.");
        }
        */

        /* Get the storage object. 
        Storage storage = StorageOptions
                .newBuilder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(cfg.getGcpKey().getBytes())))
                .setProjectId("double-folio-260711")
                .build()
                .getService();
                */

        Storage storage = StorageOptions.getDefaultInstance().getService();

        /* Generate new file name for storage and prepare the file for uploading. */
        String storageFileName = UUID.randomUUID().toString() + "." + "png";

        InputStream fileStream = imageFile;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] readBuf = new byte[4096];
        while (fileStream.available() > 0) {
            int bytesRead = fileStream.read(readBuf);
            outputStream.write(readBuf, 0, bytesRead);
        }
        byte[] storageFileData = outputStream.toByteArray();

        /* Upload and return the url. */
        BlobInfo blobInfo =
        storage.create(
            BlobInfo
                .newBuilder("instagram2-storage", storageFileName)
                // Modify access list to allow all users with link to read file
                .setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER))))
                .build(),
            outputStream.toByteArray());
        // return the public download link
   
        return blobInfo.getMediaLink();   
    }
}

