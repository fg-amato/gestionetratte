package it.prova.gestionetratte.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import it.prova.gestionetratte.model.StatoTratta;
import it.prova.gestionetratte.model.Tratta;
import it.prova.gestionetratte.repository.tratta.TrattaRepository;
import it.prova.gestionetratte.web.api.exception.TrattaNonAnnullataException;
import it.prova.gestionetratte.web.api.exception.TrattaNotFoundException;

public class TrattaServiceImpl implements TrattaService {

	@Autowired
	private TrattaRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Tratta> listAllElements(boolean eager) {
		if (eager)
			return (List<Tratta>) repository.findAllTrattaEager();

		return (List<Tratta>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Tratta caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Tratta caricaSingoloElementoEager(Long id) {
		return repository.findSingleTrattaEager(id);
	}

	@Override
	@Transactional
	public Tratta aggiorna(Tratta trattaInstance) {
		return repository.save(trattaInstance);
	}

	@Override
	@Transactional
	public Tratta inserisciNuovo(Tratta trattaInstance) {
		return repository.save(trattaInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Tratta trattaInstance) {
		repository.delete(trattaInstance);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Tratta> findByExampleWithPagination(Tratta example, Integer pageNo, Integer pageSize, String sortBy) {
		Specification<Tratta> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotEmpty(example.getCodice()))
				predicates.add(cb.like(cb.upper(root.get("codice")), "%" + example.getCodice().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getDescrizione()))
				predicates.add(
						cb.like(cb.upper(root.get("descrizione")), "%" + example.getDescrizione().toUpperCase() + "%"));

			if (example.getOraDecollo() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("oraDecollo"), example.getOraDecollo()));

			if (example.getOraAtterraggio() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("oraAtterraggio"), example.getOraAtterraggio()));

			if (example.getData() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("data"), example.getData()));

			if (example.getStato() != null)
				predicates.add(cb.equal(root.get("stato"), example.getStato()));

			if (example.getAirbus() != null && example.getAirbus().getId() != null)
				predicates.add(cb.equal(cb.upper(root.get("airbus")), example.getAirbus().getId()));
			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

		Pageable paging = null;
		// se non passo parametri di paginazione non ne tengo conto
		if (pageSize == null || pageSize < 10)
			paging = Pageable.unpaged();
		else
			paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

		return repository.findAll(specificationCriteria, paging);
	}

	@Override
	public void rimuovi(Long idTrattaToRemove) {
		Tratta toRemove = repository.findSingleTrattaEager(idTrattaToRemove);
		if (toRemove == null) {
			throw new TrattaNotFoundException("Tratta con id: " + idTrattaToRemove + " non trovato");
		}
		if (toRemove.getStato() != StatoTratta.ANNULLATA) {
			throw new TrattaNonAnnullataException(
					"La tratta con id: " + idTrattaToRemove + " non è in stato disattivato");
		}
		repository.deleteById(idTrattaToRemove);
	}

}
