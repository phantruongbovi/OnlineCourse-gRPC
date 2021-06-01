package com.github.simplesteph.grpc.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;

import static com.mongodb.client.model.Filters.eq;

public class BlogServerImpl extends BlogServiceGrpc.BlogServiceImplBase {
    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(BlogRequest request, StreamObserver<BlogResponse> responseObserver) {
        Blog blog = request.getBlog();

        System.out.println("Create document ...");
        // Create new document from request
        Document doc = new Document("author_id", blog.getAuthId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());
        System.out.println("Create doc completed, and adding doc to collection ...");

        // add document to collection
        collection.insertOne(doc);

        // get id from document
        String id = doc.getObjectId("_id").toString();
        System.out.println("Add to collection complete");

        // Create blog response
        BlogResponse blogResponse = BlogResponse.newBuilder().setBlog(
                blog.toBuilder().setId(id).build()
        ).build();
        System.out.println("Successfully response to client!");
        responseObserver.onNext(blogResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        System.out.println("Received Read Blog request!");
        String blogId = request.getBlogId();

        System.out.println("Searching doc in collection!");
        Document result = null;
        try{
            result = collection.find(eq("_id", new ObjectId(blogId)))
                    .first();
        }
        catch (Exception e){
            System.out.println("Blog has not been found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the corresponding id was not found!")
                            .asRuntimeException()
            );
        }

        if(result == null){
            // Don't have a match
            System.out.println("Blog has not been found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the corresponding id was not found!")
                            .asRuntimeException()
            );
        }
        else{
            System.out.println("Blog has been found");
            Blog blog = Blog.newBuilder()
                    .setAuthId(result.getString("author_id"))
                    .setTitle(result.getString("title"))
                    .setContent(result.getString("content"))
                    .setId(blogId)
                    .build();

            responseObserver.onNext(
                    ReadBlogResponse.newBuilder().setBlog(blog).build()
            );
            responseObserver.onCompleted();
        }

    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        System.out.println("Received Update Blog request!");
        String blogId = request.getBlog().getId();
        Blog blog = request.getBlog();

        System.out.println("Searching doc in collection!");
        Document result = null;
        try{
            result = collection.find(eq("_id", new ObjectId(blogId)))
                    .first();
        }
        catch (Exception e){
            System.out.println("Blog has not been found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the corresponding id was not found!")
                            .asRuntimeException()
            );
        }

        if(result == null){
            // Don't have a match
            System.out.println("Blog has not been found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the corresponding id was not found!")
                            .asRuntimeException()
            );
        }
        else {
            Document replaceDoc = new Document("author_id", blog.getAuthId())
                    .append("title", blog.getTitle())
                    .append("content", blog.getContent())
                    .append("_id", new ObjectId(blogId));

            System.out.println("System replace a blog!");
            collection.replaceOne(eq("_id", result.getObjectId("_id")), replaceDoc);

            System.out.println("System updated a blog successful!");
            responseObserver.onNext(
                    UpdateBlogResponse.newBuilder().setBlog(documentToBlog(replaceDoc)).build()
            );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
        System.out.println("Received Delete Blog request!");
        String blogId = request.getBlogId();

        System.out.println("Searching doc in collection!");
        Document result = null;
        try{
            result = collection.find(eq("_id", new ObjectId(blogId)))
                    .first();

        }
        catch (Exception e){
            System.out.println("Blog has not been found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the corresponding id was not found!")
                            .asRuntimeException()
            );
        }

        if(result == null){
            // Don't have a match
            System.out.println("Blog has not been found");
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with the corresponding id was not found!")
                            .asRuntimeException()
            );
        }
        else {
            collection.deleteOne(result);
            System.out.println("Id " + blogId.toString() + " deleted!");
            responseObserver.onNext(
                    DeleteBlogResponse.newBuilder().setBlogId(blogId).build()
            );
            responseObserver.onCompleted();
        }
    }


    @Override
    public void listBlog(ListBlogRequest request, StreamObserver<ListBlogReponse> responseObserver) {
        System.out.println("Received List Blog Request!");

        collection.find().iterator().forEachRemaining(document -> responseObserver.onNext(
                    ListBlogReponse.newBuilder().setBlog(documentToBlog(document)).build()
            )
        );

        responseObserver.onCompleted();
    }

    private Blog documentToBlog(Document doc){
        return Blog.newBuilder()
                .setAuthId(doc.getString("author_id"))
                .setTitle(doc.getString("title"))
                .setContent(doc.getString("content"))
                .setId(doc.getObjectId("_id").toString())
                .build();
    }
}
