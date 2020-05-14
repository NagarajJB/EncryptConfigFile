package com.manh.encryptconfigfile.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manh.encryptconfigfile.utils.JSONUtils;
import com.manh.encryptconfigfile.utils.TripleDESUtil;

@RestController
@RequestMapping("/file")
public class EncryptFileController {

	@Autowired
	private TripleDESUtil tripleDESUtil;

	@GetMapping("/downloadSample")
	public ResponseEntity<ByteArrayResource> getSampleConfigJsonFile(HttpServletResponse response) throws IOException {
		File sampleFile = ResourceUtils.getFile("classpath:sample_config.json");
		Path path = Paths.get(sampleFile.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
		return ResponseEntity.ok().contentLength(sampleFile.length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sampleFile.getName() + "\"")
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}

	@PostMapping("/encryptJsonFile")
	public ResponseEntity<ByteArrayResource> encryptFileAndDownload(@RequestParam("file") MultipartFile file)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {

		String contentAsJson = new String(file.getBytes(), StandardCharsets.UTF_8);
		if (JSONUtils.isJSONValid(contentAsJson)) {
			String encryptedContent = tripleDESUtil.harden(contentAsJson);
			String fileName = "encrypted_config.txt";
			ByteArrayResource resource = new ByteArrayResource(encryptedContent.getBytes(Charset.forName("UTF-8")));
			return ResponseEntity.ok().contentLength(encryptedContent.length())
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
					.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
		} else {
			throw new IllegalArgumentException("Not a valid json file input..");
		}
	}
}
