package br.com.alura.microservice.loja.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.alura.microservice.loja.client.EmailClient;
import br.com.alura.microservice.loja.client.FornecedorClient;
import br.com.alura.microservice.loja.dto.CompraDTO;
import br.com.alura.microservice.loja.dto.EmailDTO;
import br.com.alura.microservice.loja.dto.InfoFornecedorDTO;
import br.com.alura.microservice.loja.dto.InfoPedidoDTO;
import br.com.alura.microservice.loja.dto.ItemDoPedidoDTO;
import br.com.alura.microservice.loja.model.Compra;

@Service
public class CompraService {
	
	/*@Autowired
	private RestTemplate client;*/
	
	/*@Autowired
	private DiscoveryClient eurekaClient;*/
	
	@Autowired
	private FornecedorClient fornecedorClient;
	
	@Autowired
	private EmailClient emailClient;
	
	private static final Logger LOG = LoggerFactory.getLogger(CompraService.class);

	public Compra realizaCompra(CompraDTO compraDTO) {
		ResponseEntity<InfoFornecedorDTO> info =  fornecedorClient.getInfoPorEstado(compraDTO.getEndereco().getEstado());
		ResponseEntity<EmailDTO> email = emailClient.sendingEmail(compraDTO.getEmail());
		LOG.info("Email enviado para: " + email.getBody().getEmailFrom());
		LOG.info("buscando informações de {}",info);
		
		LOG.info("Realizando um pedido");
		InfoPedidoDTO infoPedido = fornecedorClient.realizaPedido(compraDTO.getItens());
		
		Compra compra = new Compra();
		compra.setPedidoId(infoPedido.getId());
		compra.setTempoDePreparo(infoPedido.getTempoDePreparo());
		compra.setEnderecoDestino(compraDTO.getEndereco().toString());
		
		return compra;
		
		/*ResponseEntity<InfoFornecedorDTO> exchange = 
		client.exchange("http://fornecedor/info/"+compraDTO.getEndereco().getEstado(),
				HttpMethod.GET,null,InfoFornecedorDTO.class);*/
		
		/*eurekaClient.getInstances("fornecedor").stream().forEach(fornecedor -> {
			System.out.println("localhost:"+fornecedor.getPort());
		}
	);*/
		
	}
	
    @KafkaListener(topics = "topic-pedido",groupId = "group-1",containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String,String> payload) {
    	EmailDTO email = new EmailDTO();
    	email.setOwnerRef("Anderson");
    	email.setEmailFrom("emailparaspring@gmail.com");
    	email.setEmailTo("afcaconforto@gmail.com");
    	email.setSubject("Teste de SpringBoot");
        Gson gson = new Gson();
        var pedido = gson.fromJson(payload.value(),ItemDoPedidoDTO[].class);
        email.setText(pedido.toString());
        
        emailClient.sendingEmail(email);
    }

}
