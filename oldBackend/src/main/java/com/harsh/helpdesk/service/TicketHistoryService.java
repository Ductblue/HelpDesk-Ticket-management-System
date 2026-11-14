package com.harsh.helpdesk.service;

import com.harsh.helpdesk.dto.response.TicketHistoryResponseDTO;

import java.util.List;

public interface TicketHistoryService {

    List<TicketHistoryResponseDTO> getHistoryByTicket(Long ticketId, String email);

    List<TicketHistoryResponseDTO> getMyHistory(String email);
}
