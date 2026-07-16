package com.example.sharedlib.storage;

import java.io.InputStream;

/**
 * Abstraction for file storage. Implementations may use MinIO (S3-compatible) or external S3.
 */
public interface FileStorageService {

    /**
     * Uploads a file to the storage.
     *
     * @param bucket      the bucket name
     * @param objectName  the object key/path (e.g. "projects/123/doc.pdf")
     * @param inputStream the file content
     * @param contentType MIME type
     * @param size        file size in bytes
     * @return the URL or path to access the uploaded file
     */
    String upload(String bucket, String objectName, InputStream inputStream, String contentType, long size);

    /**
     * Downloads a file from storage.
     *
     * @param bucket     the bucket name
     * @param objectName the object key/path
     * @return InputStream of the file content
     */
    InputStream download(String bucket, String objectName);

    /**
     * Deletes a file from storage.
     *
     * @param bucket     the bucket name
     * @param objectName the object key/path
     */
    void delete(String bucket, String objectName);

    /**
     * Generates a pre-signed URL for temporary access.
     *
     * @param bucket     the bucket name
     * @param objectName the object key/path
     * @param expirySeconds URL validity in seconds
     * @return pre-signed URL string
     */
    String getPresignedUrl(String bucket, String objectName, int expirySeconds);

    /**
     * Ensures the bucket exists, creating it if necessary.
     */
    void ensureBucketExists(String bucket);
}