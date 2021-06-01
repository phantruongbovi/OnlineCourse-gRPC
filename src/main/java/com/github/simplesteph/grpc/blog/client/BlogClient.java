package com.github.simplesteph.grpc.blog.client;

import com.github.simplesteph.grpc.calculator.client.CalculatingClient;
import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.net.ssl.SSLException;
import java.util.Iterator;

public class BlogClient {
    public static void main(String[] args) throws SSLException {
        BlogClient main = new BlogClient();
        main.run();
    }

    private void run(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        /// Create new blog
        System.out.println("1. Create new Blog");
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
        Blog blog = Blog.newBuilder()
                .setAuthId("Truong")
                .setTitle("New blog!")
                .setContent("Hello world! This is my first blog")
                .build();

        BlogResponse createBlog = blogClient.createBlog(
                BlogRequest.newBuilder().setBlog(blog).build()
        );

        System.out.println("Successfully request and claim response!");
        System.out.println(createBlog.toString());

        /// Find blog
            // blog can found
        String blogId = createBlog.getBlog().getId();
        ReadBlogResponse readResponse = blogClient.readBlog(
                ReadBlogRequest.newBuilder().setBlogId(blogId).build()
        );
        System.out.println("The result from " + blogId + " is: ");
        System.out.println(readResponse.toString());

        // blog can not found
        /*String blogId1 = "blogId1";
        ReadBlogResponse readResponse1 = blogClient.readBlog(
                ReadBlogRequest.newBuilder().setBlogId(blogId1).build()
        );
        System.out.println("The result from " + blogId + " is: ");
        System.out.println(readResponse.toString());*/

        /// Update Blog
        System.out.println("2. Update Blog");
        Blog newBlog = Blog.newBuilder()
                .setId(blogId)
                .setAuthId("Truong handsome")
                .setTitle("New blog(Update)!")
                .setContent("Hello world! This is my second blog")
                .build();
        System.out.println("Updating Blog!");
        UpdateBlogResponse response = blogClient.updateBlog(UpdateBlogRequest.newBuilder().setBlog(newBlog).build());
        System.out.println("Updated Blog!");
        System.out.println(response.toString());

        /// Delete Blog
        //blogId
        System.out.println("3. Delete Blog");
        System.out.println("Sending id to database for deleting document!");
        DeleteBlogResponse deleteBlogResponse = blogClient.deleteBlog(
                DeleteBlogRequest.newBuilder().setBlogId(blogId).build()
        );
        System.out.println("Sending successfully and the result id: " + deleteBlogResponse.getBlogId().toString() + " deleted");


        // List Blog
        System.out.println("4. List Blog");
        System.out.println("List blog in collection: ");
       blogClient.listBlog(
                ListBlogRequest.newBuilder().build()
        ).forEachRemaining(
                listBlogReponse -> System.out.println(listBlogReponse.getBlog().toString())
       );


    }
}
