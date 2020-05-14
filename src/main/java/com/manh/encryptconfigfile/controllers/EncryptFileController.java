package com.manh.encryptconfigfile.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manh.encryptconfigfile.utils.JSONUtils;
import com.manh.encryptconfigfile.utils.TripleDESUtil;

@RestController
@RequestMapping("/encrypt")
public class EncryptFileController {

	@Autowired
	private TripleDESUtil tripleDESUtil;

	@PostMapping("/file")
	public void encryptFileAndDownload(Model model, @RequestParam("file") MultipartFile file,
			HttpServletResponse response) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		String contentAsJson = new String(file.getBytes(), StandardCharsets.UTF_8);
		System.out.println(contentAsJson);

		if (JSONUtils.isJSONValid(contentAsJson)) {
			String encryptedContent = tripleDESUtil.harden(contentAsJson);
			System.out.println(encryptedContent);

			String fileName = "encrypted_config.txt";
			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			response.setContentLength(encryptedContent.length());
			response.getWriter().write(encryptedContent);
		} else {
			throw new IllegalArgumentException("Not a valid json file input..");
		}

	}

}
