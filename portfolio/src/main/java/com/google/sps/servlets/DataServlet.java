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

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    public class Comment {
        public String timestamp;
        public String email; // to be revised
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
    for (Entity comments: results.asIterable()) {
        response.getWriter().println(comments.getProperty("timestamp"));
        response.getWriter().println(comments.getProperty("email"));
        response.getWriter().println(comments.getProperty("content"));
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String comment = getParameter(request, "text-input", "");
      String email;
      boolean Friend = Boolean.parseBoolean(getParameter(request, "friend", "false"));
      System.out.println(Friend);
      if (Friend) {
          email = getParameter(request, "email-input", "anonymous");
      } 
      else email = "anonymous";
      SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");
        Date date = new Date();
        String timestamp = sdf.format(date);
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("email", email);
      commentEntity.setProperty("content", comment);
      commentEntity.setProperty("timestamp", timestamp);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
      //response.sendRedirect("https://www.baidu.com");
      response.sendRedirect("/index.html");
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    System.out.println(value);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
