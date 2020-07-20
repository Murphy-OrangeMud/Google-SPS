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
import java.io.UnsupportedEncodingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import java.net.URLDecoder;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

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

    response.setContentType("text/html; charset=utf-8");

    for (Entity comments: results.asIterable()) {
        String timestamp = (String) comments.getProperty("timestamp");
        int year = Integer.parseInt(timestamp.substring(0,4));
        int month = Integer.parseInt(timestamp.substring(5,7));
        int day = Integer.parseInt(timestamp.substring(8,10));
        if (year <= 2020 && month <= 7 && day <= 19) continue;
        String image_url = (String) comments.getProperty("image");
        response.getWriter().println("<article class = \"comment-body\">");
        response.getWriter().println("<footer class = \"comment-metadata\">");
        response.getWriter().println("<div class = \"comment-author\">");
        response.getWriter().println("<img width=\"80\" height=\"80\" src=\"" + image_url + "\" />");
        String name = (String) comments.getProperty("name");
        String email = (String) comments.getProperty("email");
        response.getWriter().println("<b class=\"fn\">" + name + "</b>");
        response.getWriter().println("<time>" + timestamp + "</time></div></footer>");
        response.getWriter().println("<div class=\"comment-content\"><p>");
        response.getWriter().println(comments.getProperty("content") + "</p></div></article>");
               
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      //String comment = Jsoup.clean(request.getParameter("text-input"), Whitelist.none(), Document.outputSettings().charset("UTF-8"));
      String pattern = "<.*>.*</.*>";
      String comment = getParameter(request, "text-input", "");

      boolean badguy = Pattern.matches(pattern, comment);

      if (badguy) {
          response.sendRedirect("/badguy");
          return;
      }

      String name = getParameter(request, "name", "");
      String email = getParameter(request, "email", "");
      
      if (comment.equals("") || name.equals("") || email.equals("")) {
          response.sendRedirect("/anonymous");
          return;
      }

      SimpleDateFormat sdf = new SimpleDateFormat();
      sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");
      Date date = new Date();
      String timestamp = sdf.format(date);

      String imageUrl = getUploadedImageUrl(request, "image");
      
      if (imageUrl == null) {
          imageUrl = "/image/anonymous.jpg";
      }

      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("name", name);
      commentEntity.setProperty("email", email);
      commentEntity.setProperty("content", comment);
      commentEntity.setProperty("timestamp", timestamp);
      commentEntity.setProperty("image", imageUrl);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);

      commented = true;
      response.sendRedirect("/thanks");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) throws UnsupportedEncodingException {
    request.setCharacterEncoding("utf-8");
    //String value = URLDecoder.decode(request.getParameter(name), "utf-8");
    //
    String value = new String(request.getParameter(name).getBytes("iso8859-1"),"utf-8");
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
