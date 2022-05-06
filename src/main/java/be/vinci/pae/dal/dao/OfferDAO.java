package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.OfferDTO;
import java.util.List;
import java.util.Map;

public interface OfferDAO {

  /**
   * Get all offers.
   *
   * @param searchPattern the search pattern (empty -> all) according to their type, description,
   *                      username and lastname
   * @param idMember      the member id if you want only your offers (0 -> all)
   * @param type          the type of object that we want
   * @param dateText      the max date late
   * @param objectStatus  the status of object that we want
   * @return list of offers
   */
  List<OfferDTO> getAll(String searchPattern, int idMember, String type, String objectStatus,
                          String dateText);

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  List<OfferDTO> getAllLast();

  /**
   * Get the offer with a specific id.
   *
   * @param idOffer the id of the offer
   * @return an offer that match with the idOffer or null
   */
  OfferDTO getOne(int idOffer);


  /**
   * Get last offer of an object.
   *
   * @param idObject the id of the object
   * @return an offer
   */
  OfferDTO getLastObjectOffer(int idObject);

  /**
   * Add an offer in the db.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  OfferDTO addOne(OfferDTO offerDTO);

  /**
   * Update the time slot of an offer.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot or null
   */
  OfferDTO updateOne(OfferDTO offerDTO);

  /**
   * Get all offers received by a member.
   *
   * @param idReceiver the id of the receiver
   * @return a list of offerDTO
   */
  List<OfferDTO> getAllGivenOffers(int idReceiver);

  /**
   * Get all offers received by a member.
   *
   * @param idReceiver the id of the receiver
   * @param searchPattern the search pattern (empty -> all) according to their type, description
   * @return a list of offerDTO
   */
  List<OfferDTO> getAllGivenAndAssignedOffers(int idReceiver, String searchPattern);

  /**
   * Get a map of data about a member (nb of received object, nb of not colected objects, nb of
   * given objects and nb of total offers).
   *
   * @param idReceiver the id of the member
   * @return a map with all th datas.
   */
  Map<String, Integer> getOffersCount(int idReceiver);
}
