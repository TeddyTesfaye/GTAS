/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.gtas.model.dto.PassengerNoteDto;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "passenger_notes")
public class PassengerNote extends Note {

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cmt_passenger_id", updatable = false, insertable = false)
	private Passenger passenger;

	@JsonIgnore
	@Column(name = "cmt_passenger_id", columnDefinition = "bigint unsigned")
	private Long passengerId;

	public static PassengerNote from(PassengerNoteDto note, String userId) {
		PassengerNote passengerNote = new PassengerNote();
		passengerNote.setPassengerId(note.getPassengerId());
		passengerNote.setCreatedAt(new Date());
		passengerNote.setPlainTextComment(note.getPlainTextNote());
		passengerNote.setRtfComment(note.getRtfNote());
		passengerNote.setCreatedBy(userId);
		if (!note.getNoteTypeSet().isEmpty()) {
			passengerNote.getNoteType().addAll(note.getNoteTypeSet());
		}
		return passengerNote;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

}
