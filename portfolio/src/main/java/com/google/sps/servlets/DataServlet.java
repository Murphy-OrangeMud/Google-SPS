// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private boolean commented = false;

    public class Comment {
        public String timestamp;
        public String email;
        public String content;
        
        public Comment() {}
        public Comment(String _timestamp, String _email, String _content) {
            timestamp = _timestamp;
            email = _email;
            content = _content;
        }
    }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    response.setContentType("text/html; charset=UTF-8");

    if (commented) {
        response.getWriter().println("<script>alert(\"感谢你的支持！\");</script>");
        commented = false;
    }

    for (Entity comments: results.asIterable()) {
        String timestamp = (String) comments.getProperty("timestamp");
        int year = Integer.parseInt(timestamp.substring(0,4));
        int month = Integer.parseInt(timestamp.substring(5,7));
        int day = Integer.parseInt(timestamp.substring(8,10));
        if (year <= 2020 && month <= 7 && day <= 10) continue;
        response.getWriter().println(timestamp + "<br></br>");
        String tmp_email = (String) comments.getProperty("email");
        int nickname = tmp_email.indexOf('@');
        if (nickname != -1) { 
            tmp_email = tmp_email.substring(0, nickname); 
        }
        response.getWriter().println(tmp_email + "<br></br>");
        response.getWriter().println(comments.getProperty("content") + "<br></br>");
        String image_url = (String) comments.getProperty("image");
        if (image_url != null) {
            response.getWriter().println("<img style=\"height=50px;\" src=\"" + image_url + "\" /><br></br>");
        }       
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String comment = Jsoup.clean(request.getParameter("text-input"), Whitelist.none());
      String email;
      boolean Friend = Boolean.parseBoolean(getParameter(request, "friend", "false"));

      if (Friend) {
          email = getParameter(request, "email-input", "anonymous");
      } 
      else email = "anonymous";

      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");
      Date date = new Date();
      String timestamp = sdf.format(date);

      String imageUrl = getUploadedImageUrl(request, "image");

      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("email", email);
      commentEntity.setProperty("content", comment);
      commentEntity.setProperty("timestamp", timestamp);
      commentEntity.setProperty("image", imageUrl);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);

      commented = true;
      response.sendRedirect("/index.html");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  private String getUploadedImageUrl(HttpServletRequest request, String name) {
      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
      List<BlobKey> blobKeys = blobs.get(name);

      if (blobKeys == null || blobKeys.isEmpty()) return null;

      BlobKey blobKey = blobKeys.get(0);

      BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
      if (blobInfo.getSize() == 0) {
        blobstoreService.delete(blobKey);
        return null;
      }

      ImagesService imagesService = ImagesServiceFactory.getImagesService();
      ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
      String url = imagesService.getServingUrl(options);

      if(url.startsWith("http://localhost:8080/")){
        url = url.replace("http://localhost:8080/", "/");
      }
      return url;
  }
}
