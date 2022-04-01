package br.com.alura.microservice.loja.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import br.com.alura.microservice.loja.dto.EmailDTO;

@FeignClient("email")
public interface EmailClient {
	
	
	@PostMapping(value = "/email/sending-email")
	public ResponseEntity<EmailDTO> sendingEmail(EmailDTO emailDto);

}
