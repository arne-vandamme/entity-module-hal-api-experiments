package com.example.demo.application.domain.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PetType implements Persistable<Long>
{
	private Long id;
	private String name;

	@Override
	public boolean isNew() {
		return id == null;
	}
}
