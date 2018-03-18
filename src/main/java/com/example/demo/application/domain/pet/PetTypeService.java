package com.example.demo.application.domain.pet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
@Service
@RequiredArgsConstructor
public class PetTypeService
{
	private final RestTemplate petApiClient;

	public List<PetType> findAll() {
		return Arrays.asList( petApiClient.getForObject( "/pettypes", PetType[].class ) );
	}

	public PetType getById( Serializable id ) {
		return petApiClient.getForObject( "/pettypes/" + id, PetType.class );
	}

	public PetType save( PetType petType ) {
		if ( petType.isNew() ) {
			return petApiClient.postForObject( "/pettypes", petType, PetType.class );
		}

		HttpEntity<PetType> postData = new HttpEntity<>( petType );
		petApiClient.exchange( "/pettypes/" + petType.getId(), HttpMethod.PUT, postData, Void.class );
		//petApiClient.put(  ).postForEntity( "/pettypes/" + petType.getId(), petType );

		return petType;
	}
}
