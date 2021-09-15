package com.metalheart.model.foo

import com.metalheart.model.dto.ConfirmationDTO
import com.metalheart.model.dto.InputDTO

data class ClientNetcodeData(val inputs: Set<InputDTO>,
                             val acknowledgements: Set<ServerAck>,
                             val confirmations: Set<ConfirmationDTO>)


data class ServerNetcodeData(val inputs: Set<InputDTO>,
                             val acknowledgements: Set<ServerAck>,
                             val confirmations: Set<ConfirmationDTO>)



