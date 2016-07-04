package org.topcat.docserver.web;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/files")
public class FileController {

    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public FileController(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<byte[]> createOrUpdate(@RequestParam("file") MultipartFile file) {
        String name = file.getOriginalFilename();
        try {
            GridFSFile gridFSFile = gridFsTemplate.store(file.getInputStream(), name, file.getContentType());
            gridFSFile.save();
            String resp = gridFSFile.getId().toString();
            return new HttpEntity<>(resp.getBytes());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<String> list() {
        return getFiles().stream()
                .map(GridFSDBFile::getFilename)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/{fileID}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.MOVED_PERMANENTLY)
    public void get(@PathVariable("fileID") String fileID, HttpServletResponse res) {
        res.setHeader("Location", "//192.168.1.11:8080/webroot/fs/"+fileID+".pdf");
//        try {
//            Optional<GridFSDBFile> optionalCreated = maybeLoadFile(fileID);
//            if (optionalCreated.isPresent()) {
//                GridFSDBFile created = optionalCreated.get();
//                OutputStream os = res.getOutputStream();
//                created.writeTo(os);
//                res.setHeader(HttpHeaders.CONTENT_TYPE, created.getContentType());
//                os.flush();
//            } else {
//                res.setStatus(HttpStatus.NOT_FOUND.value());
//            }
//        } catch (IOException e) {
//            res.setStatus(HttpStatus.IM_USED.value());
//        }
    }

    private List<GridFSDBFile> getFiles() {
        return gridFsTemplate.find(null);
    }

    private Optional<GridFSDBFile> maybeLoadFile(String id) {
        GridFSDBFile file = gridFsTemplate.findOne(getIdQuery(id));
        return Optional.ofNullable(file);
    }

//    private static Query getFilenameQuery(String name) {
//        return Query.query(GridFsCriteria.whereFilename().is(name));
//    }

    private static Query getIdQuery(String id) {
        return Query.query(GridFsCriteria.where("_id").is(new ObjectId(id)));
    }
}