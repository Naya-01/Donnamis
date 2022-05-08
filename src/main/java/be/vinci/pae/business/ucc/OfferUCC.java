package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import java.util.List;
import java.util.Map;

public interface OfferUCC {

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  List<OfferDTO> getLastOffers();

  /**
   * Get the offer with a specific id.
   *
   * @param idOffer the id of the offer
   * @return an offer that match with the idOffer or an error if offer not found
   */
  OfferDTO getOfferById(int idOffer);

  /**
   * Add an offer for an object.
   *
   * @param offerDTO an offer we want to add
   * @param ownerDTO member object
   * @return the offerDTO added
   */
  OfferDTO addOffer(OfferDTO offerDTO, MemberDTO ownerDTO);

  /**
   * Update the time slot of an offer or an error code.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot
   */
  OfferDTO updateOffer(OfferDTO offerDTO);

  /**
   * Get all offers.
   *
   * @param search       the search pattern (empty -> all) according to their type, description
   * @param idMember     the member id if you want only your offers (0 -> all)
   * @param type         the type of object that we want
   * @param objectStatus the status of object that we want
   * @param dateText      the max date late
   * @return list of offers
   */
  List<OfferDTO> getOffers(String search, int idMember, String type, String objectStatus,
      String dateText);

  /**
   * Return the last offer of an object.
   *
   * @param idObject to search.
   * @return last offer.
   */
  OfferDTO getLastOffer(int idObject);

  /**
   * Get all offers received by a member.
   *
   * @param idReceiver the id of the receiver
   * @return a list of offerDTO
   */
  List<OfferDTO> getGivenOffers(int idReceiver);

  /**
   * Get all offers received by a member.
   *
   * @param receiver the receiver
   * @param searchPattern the search pattern (empty -> all) according to their type, description
   * @return a list of offerDTO
   */
  List<OfferDTO> getGivenAndAssignedOffers(MemberDTO receiver, String searchPattern);

  /**
   * Cancel an Object.
   *
   * @param offerDTO object with his id & set the status to 'cancelled'
   * @param ownerDTO member object
   * @return an object
   */
  OfferDTO cancelOffer(OfferDTO offerDTO, MemberDTO ownerDTO);


  /**
   * Mark an object to 'not collected'.
   *
   * @param offerDTO object with his id & set the status to 'not collected'
   * @param ownerDTO member object
   * @return an object
   */
  OfferDTO notCollectedOffer(OfferDTO offerDTO, MemberDTO ownerDTO);

  /**
   * Give an Object, set the status to 'given'.
   *
   * @param offerDTO : object with his id'
   * @param ownerDTO member object
   * @return an object
   */
  OfferDTO giveOffer(OfferDTO offerDTO, MemberDTO ownerDTO);

  /**
   * Get a map of data about a member (nb of received object, nb of not colected objects, nb of
   * given objects and nb of total offers).
   *
   * @param idReceiver the id of the member
   * @return a map with all th datas.
   */
  Map<String, Integer> getOffersCount(int idReceiver);

  /**
   * Make an Object with his offer.
   *
   * @param offerDTO object that contain id object & offerDTO information
   * @return offer
   */
  OfferDTO addObject(OfferDTO offerDTO);
}
