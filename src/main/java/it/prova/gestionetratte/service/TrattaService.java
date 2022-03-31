package it.prova.gestionetratte.service;

import java.util.List;

import org.springframework.data.domain.Page;

import it.prova.gestionetratte.model.Tratta;

public interface TrattaService {
	List<Tratta> listAllElements(boolean eager);

	Tratta caricaSingoloElemento(Long id);

	Tratta caricaSingoloElementoEager(Long id);

	Tratta aggiorna(Tratta trattaInstance);

	Tratta inserisciNuovo(Tratta trattaInstance);

	void rimuovi(Tratta trattaInstance);

	public Page<Tratta> findByExampleWithPagination(Tratta example, Integer pageNo, Integer pageSize, String sortBy);

	public void rimuovi(Long idTrattaToRemove);

	public List<Tratta> annullaTratte();
}
