package v1.resources;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Base64;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import org.apache.commons.codec.digest.*;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("image-processor")
public class ImageProcessorResource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addNewImage(InputStream imageFile) {
        
        /* Upload the image to the GCP Storage and get the url. */
        try {
            String url = s3Upload(imageFile);
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

    private String s3Upload(InputStream imageFile) throws Exception {

        AWSCredentials credentials = new BasicAWSCredentials(
            "AKIAVSWZG4K4PGYBJIVX", "4+ePbrDbA3JuZbIWcjpEi8a1FtEptyaOT4aXnJxR"
        );

        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        String bucketName = "instagram2-storage";

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
        

        ObjectMetadata metadata = new ObjectMetadata();
        byte[] resultByte = DigestUtils.md5(storageFileData);
        String streamMD5 = new String(Base64.getEncoder().encode(resultByte));
        metadata.setContentMD5(streamMD5);
        metadata.setContentLength(Long.valueOf(storageFileData.length));

        s3Client.putObject(bucketName, storageFileName, new ByteArrayInputStream(outputStream.toByteArray()), metadata);

        return storageFileName;
    }
}

