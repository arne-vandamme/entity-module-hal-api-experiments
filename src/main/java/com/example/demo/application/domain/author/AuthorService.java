package com.example.demo.application.domain.author;

import com.example.demo.application.domain.HypermediaApiConnector;
import com.foreach.across.core.annotations.PostRefresh;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Arne Vandamme
 * @since ${projectVersion}
 */
@Service
public class AuthorService extends HypermediaApiConnector<AuthorResource>
{
	public AuthorService() {
		super( "/authors", AuthorResource.class );
	}

	@PostRefresh
	@Override
	public void setApiClient( RestTemplate bookApiClient ) {
		super.setApiClient( bookApiClient );
	}
}
