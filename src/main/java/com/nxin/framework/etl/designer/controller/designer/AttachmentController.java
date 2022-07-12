package com.nxin.framework.etl.designer.controller.designer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxin.framework.etl.designer.dto.CrudDto;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.entity.designer.Shell;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.service.designer.ShellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.*;

@PreAuthorize("hasAuthority('ROOT') or hasAuthority('ATTACHMENT')")
@RestController
@RequestMapping("/attachment")
public class AttachmentController {
    @Autowired
    private UserService userService;
    @Autowired
    private ShellService shellService;
    @Value("${attachment.dir}")
    private String attachmentDir;
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile multipartFile, Principal principal) throws IOException {
        User loginUser = userService.one(principal.getName());
        StringBuilder builder = new StringBuilder(attachmentDir);
        builder.append(loginUser.getTenant().getName()).append(File.separator);
        File directory = new File(builder.toString());
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filename = builder.toString().concat(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
        multipartFile.transferTo(file);
        return ResponseEntity.ok(multipartFile.getOriginalFilename());
    }

    @PostMapping("/delete")
    public ResponseEntity<Integer> delete(@RequestBody CrudDto crudDto, Principal principal) throws IOException {
        User loginUser = userService.one(principal.getName());
        Shell shell = shellService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (shell != null && shell.getProject().getUsers().contains(loginUser)) {
            StringBuilder builder = new StringBuilder(attachmentDir);
            Map<String, String> json = objectMapper.readValue(crudDto.getPayload(), new TypeReference<Map<String, String>>() {
            });
            String path = builder.append(loginUser.getTenant().getId()).append(File.separator).append(shell.getProject().getId()).append(File.separator).append(shell.getId()).append(File.separator).append(json.get("env")).append(File.separator).append(json.get("filename")).toString();
            File file = new File(path);
            if (file.exists()) {
                file.delete();
                return ResponseEntity.ok().build();
            } else {
                throw new FileNotFoundException(crudDto.getPayload());
            }
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/preview")
    public ResponseEntity<List<Map<String, Object>>> preview(@RequestBody CrudDto crudDto, Principal principal) throws IOException {
        User loginUser = userService.one(principal.getName());
        Shell shell = shellService.one(crudDto.getId(), loginUser.getTenant().getId());
        if (shell != null && shell.getProject().getUsers().contains(loginUser)) {
            StringBuilder builder = new StringBuilder(attachmentDir);
            String path = builder.append(loginUser.getTenant().getId()).append(File.separator).append(shell.getProject().getId()).append(File.separator).append(shell.getId()).append(File.separator).append(crudDto.getPayload()).append(File.separator).toString();
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            List<Map<String, Object>> records = new ArrayList<>(0);
            for (String name : folder.list()) {
                File child = new File(path + name);
                Map<String, Object> record = new HashMap<>(0);
                record.put("name", name);
                record.put("lastModified", child.lastModified());
                records.add(record);
            }
            return ResponseEntity.ok(records);
        }
        return ResponseEntity.status(Constant.EXCEPTION_UNAUTHORIZED).build();
    }

    @PostMapping("/download/{id}/{env}/{filename}")
    public void download(@PathVariable("id") long id, @PathVariable("env") String env, @PathVariable("filename") String filename, HttpServletResponse response, Principal principal) throws IOException {
        User loginUser = userService.one(principal.getName());
        Shell shell = shellService.one(id, loginUser.getTenant().getId());
        if (shell != null && shell.getProject().getUsers().contains(loginUser)) {
            StringBuilder builder = new StringBuilder(attachmentDir);
            String path = builder.append(loginUser.getTenant().getId()).append(File.separator).append(shell.getProject().getId()).append(File.separator).append(shell.getId()).append(File.separator).append(env).append(File.separator).append(filename).toString();
            File file = new File(path);
            if (file.exists()) {
                response.setContentType("application/octet-stream");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "utf-8"));
                byte[] buffer = new byte[1024];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                OutputStream os = response.getOutputStream();
                int i;
                while ((i = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, i);
                }
                os.flush();
                bis.close();
            } else {
                throw new FileNotFoundException(filename);
            }
        }
    }
}
