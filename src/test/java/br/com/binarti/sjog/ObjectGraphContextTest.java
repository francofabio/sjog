package br.com.binarti.sjog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ObjectGraphContextTest {
	
	@Test
	public void shouldIncludeNestedProperty() { 
		ObjectGraphContext context = new ObjectGraphBuilder()
				.include("id")
				.include("pessoa")
				.include("pessoa.nome")
				.include("pessoa.endereco")
				.include("pessoa.endereco.logradouro")
				.include("pessoa.endereco.tipoLogradouro")
				.include("pessoa.contatos")
				.include("pessoa.contatos.tipo")
				.include("pessoa.contatos.dado")
				.buildContext();
		assertTrue(context.included("id", true));
		assertTrue(context.included("pessoa", false));
		assertTrue(context.included("pessoa.nome", true));
		assertTrue(context.included("pessoa.endereco", false));
		assertTrue(context.included("pessoa.endereco.tipoLogradouro", true));
		assertTrue(context.included("pessoa.endereco.logradouro", true));
		assertTrue(context.included("pessoa.endereco.nada", true));
		assertTrue(context.included("pessoa.contatos", false));
		assertTrue(context.included("pessoa.contatos[0].tipo", true));
		assertTrue(context.included("pessoa.contatos[0].dado", true));
	}
	
	@Test
	public void shouldAutoIncludePrimitiveFromNestedProperty() {
		ObjectGraphContext context = new ObjectGraphBuilder()
				.include("id")
				.include("date")
				.include("delivery")
				.include("delivery.address")
				.include("delivery.forecast")
				.include("delivery.target")
				.include("delivery.target.name")
				.autoIncludePrimitives("delivery", false)
				.buildContext();
		assertFalse(context.autoIncludePrimitives("delivery"));
	}
	
	@Test
	public void shouldExcludeAllPrimitivePropertyFromRoot() {
		ObjectGraphContext context = new ObjectGraphBuilder()
				.include("id")
				.include("date")
				.include("delivery")
				.include("delivery.address")
				.include("delivery.forecast")
				.excludePrimitivesFromRoot()
				.buildContext();
		assertTrue(context.excluded("id", true, true));
		assertTrue(context.excluded("date", true, true));
		assertFalse(context.excluded("delivery", false, true));
		assertFalse(context.excluded("delivery.address", true, false));
		assertFalse(context.excluded("delivery.forecast", true, false));
	}
	
	@Test
	public void shouldExcludeNestedProperty() { 
		ObjectGraphContext context = new ObjectGraphBuilder()
				.include("id")
				.include("pessoa")
				.include("pessoa.nome")
				.include("pessoa.endereco")
				.include("pessoa.endereco.logradouro")
				.include("pessoa.endereco.tipoLogradouro")
				.include("pessoa.contatos")
				.include("pessoa.contatos.tipo")
				.include("pessoa.contatos.dado")
				.exclude("pessoa.contatos.tipo")
				.exclude("pessoa.endereco.*")
				.buildContext();
		assertFalse(context.excluded("id", true, true));
		assertFalse(context.excluded("pessoa", false, true));
		assertFalse(context.excluded("pessoa.nome", true, false));
		assertTrue(context.excluded("pessoa.endereco", false, false));
		assertTrue(context.excluded("pessoa.endereco.tipoLogradouro", true, false));
		assertTrue(context.excluded("pessoa.endereco.logradouro", true, false));
		assertTrue(context.excluded("pessoa.endereco.nada", true, false));
		assertFalse(context.excluded("pessoa.contatos", false, false));
		assertTrue(context.excluded("pessoa.contatos[0].tipo", true, false));
		assertFalse(context.excluded("pessoa.contatos[0].dado", true, false));
	}
	
}
