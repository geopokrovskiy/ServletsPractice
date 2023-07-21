package com.geopokrovskiy.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geopokrovskiy.dto.ResponseResult;
import com.geopokrovskiy.model.Event;
import com.geopokrovskiy.model.File;
import com.geopokrovskiy.model.User;
import com.geopokrovskiy.repository.EventRepository;
import com.geopokrovskiy.repository.FileRepository;
import com.geopokrovskiy.repository.UserRepository;
import com.geopokrovskiy.repository.impl.EventRepositoryImpl;
import com.geopokrovskiy.repository.impl.FileRepositoryImpl;
import com.geopokrovskiy.repository.impl.UserRepositoryImpl;
import com.geopokrovskiy.util.ServletUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventServlet extends HttpServlet {
    private FileRepository fileRepository = new FileRepositoryImpl();
    private EventRepository eventRepository = new EventRepositoryImpl();
    private UserRepository userRepository = new UserRepositoryImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String uploadFilePath = "C:\\users_files\\";

    private java.io.File file;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.setServletParams(req, resp);
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        java.io.File repository = new java.io.File(uploadFilePath);
        diskFileItemFactory.setRepository(repository);


        java.io.File tempDir = new java.io.File(this.uploadFilePath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

        ResponseResult<List<Event>> responseResult = new ResponseResult<>();


        if (ServletFileUpload.isMultipartContent(req)) {
            try {
                List<FileItem> formItems = servletFileUpload.parseRequest(req);
                List<FileItem> fileItems = new ArrayList<>();

                if (formItems != null && formItems.size() > 0) {
                    Long userId = 0L;
                    for (FileItem fileItem : formItems) {
                        if (fileItem.isFormField()) {
                            String name = fileItem.getFieldName();
                            String value = fileItem.getString();
                            if (name.equals("user_id")) {
                                userId = Long.parseLong(value);
                            }
                        } else {
                            fileItems.add(fileItem);
                        }
                    }
                    User user = this.userRepository.getById(userId);
                    if (user == null) {
                        responseResult.setMessage("User with such ID does not exist!");
                        resp.setStatus(404);
                        this.objectMapper.writeValue(resp.getWriter(), responseResult);
                        return;
                    }
                    List<Event> responseEvents = new ArrayList<>();
                    for (FileItem fileItem : fileItems) {
                        try {
                            File file = new File(0L, fileItem.getName(), this.uploadFilePath, null);
                            Event event = new Event(0L, user, file);

                            this.fileRepository.addNew(file);
                            this.eventRepository.addNew(event);

                            List<Event> fileEvents = new ArrayList<>();
                            file.setEvents(fileEvents);
                            fileEvents.add(event);
                            responseEvents.add(event);

                            fileItem.write(new java.io.File(fileItem.getName()));


                            String fileName = fileItem.getName();
                            if (fileName.lastIndexOf("\\") >= 0) {
                                this.file = new java.io.File(uploadFilePath +
                                        fileName.substring(fileName.lastIndexOf("\\")));
                            } else {
                                this.file = new java.io.File(uploadFilePath +
                                        fileName.substring(fileName.lastIndexOf("\\") + 1));
                            }
                            fileItem.write(this.file);

                            responseResult.setData(responseEvents);
                            responseResult.setMessage("OK");
                            resp.setStatus(200);
                        } catch (Exception e) {
                            responseResult.setMessage(e.getMessage());
                            resp.setStatus(500);
                        }
                    }
                }
            } catch (FileUploadException e) {
                responseResult.setMessage(e.getMessage());
                resp.setStatus(500);
            }
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletUtils.setServletParams(req, resp);
        Long idRequest = ServletUtils.processURI(req, "files");

        if (idRequest == 0) {
            ResponseResult<List<File>> responseResult = new ResponseResult<>();
            List<File> files = this.fileRepository.getAll();
            resp.setStatus(200);
            responseResult.setMessage("OK");
            responseResult.setData(files);
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
        } else if (idRequest > 0) {
            ResponseResult<File> responseResult = new ResponseResult<>();
            File file = this.fileRepository.getById(idRequest);
            if (file != null) {
                java.io.File dir = new java.io.File(this.uploadFilePath);
                ServletContext context = req.getServletContext();
                String filename = file.getName();
                String mime = context.getMimeType(filename);
                if (mime == null) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().println("Incorrect type file");
                    resp.setStatus(400);
                    return;
                }
                java.io.File toDownload = new java.io.File(dir + java.io.File.separator + filename);
                try (FileInputStream input = new FileInputStream(toDownload);
                     ServletOutputStream out = resp.getOutputStream()) {
                    resp.setContentType(mime);
                    resp.setContentLength((int) toDownload.length());
                    input.transferTo(out);
                    long length = input.transferTo(out);
                    System.out.println("Bytes transferred: " + length);
                    resp.setStatus(200);
                    responseResult.setMessage("OK");
                } catch (FileNotFoundException e) {
                    responseResult.setMessage("Incorrect file name");
                    this.objectMapper.writeValue(resp.getWriter(), responseResult);
                    resp.setStatus(400);
                } catch (IOException e) {
                    responseResult.setMessage("File Error!");
                    this.objectMapper.writeValue(resp.getWriter(), responseResult);
                    resp.setStatus(500);
                }

            } else {
                responseResult.setMessage("File not found!");
                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), responseResult);
            }
        } else {
            ResponseResult<File> responseResult = new ResponseResult<>();
            resp.setStatus(400);
            responseResult.setMessage("Incorrect id format!");
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.setServletParams(req, resp);
        Long requestId = ServletUtils.processURI(req, "files");
        ResponseResult<File> responseResult = new ResponseResult<>();
        if (requestId <= 0) {
            responseResult.setMessage("Incorrect id input!");
            resp.setStatus(400);
        } else {
            File toDelete = this.fileRepository.getById(requestId);
            if (toDelete == null) {
                responseResult.setMessage("File not found!");
                resp.setStatus(400);
            } else {
                java.io.File dir = new java.io.File(this.uploadFilePath);
                String filenameToDelete = toDelete.getName();
                java.io.File fileToDelete = new java.io.File(dir + java.io.File.separator + filenameToDelete);
                if(fileToDelete.delete()){
                    this.fileRepository.delete(toDelete.getId());
                    resp.setStatus(200);
                    responseResult.setMessage("OK");
                    responseResult.setData(toDelete);
                } else{
                    resp.setStatus(500);
                    responseResult.setMessage("Internal Server Error!");
                }
            }
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }
}
