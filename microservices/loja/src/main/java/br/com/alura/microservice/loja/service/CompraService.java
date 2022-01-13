package br.com.alura.microservice.loja.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.alura.microservice.loja.client.FornecedorClient;
import br.com.alura.microservice.loja.dto.CompraDTO;
import br.com.alura.microservice.loja.dto.InfoFornecedorDTO;
import br.com.alura.microservice.loja.dto.InfoPedidoDTO;
import br.com.alura.microservice.loja.model.Compra;

@Service
public class CompraService {
	
	/*@Autowired
	private RestTemplate client;*/
	
	/*@Autowired
	private DiscoveryClient eurekaClient;*/
	
	@Autowired
	private FornecedorClient fornecedorClient;

	public Compra realizaCompra(CompraDTO compraDTO) {
		ResponseEntity<InfoFornecedorDTO> info =  fornecedorClient.getInfoPorEstado(compraDTO.getEndereco().getEstado());
		System.out.println(info.getBody().getEndereco());
		
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

}
