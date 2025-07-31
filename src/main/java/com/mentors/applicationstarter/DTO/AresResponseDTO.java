package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AresResponseDTO {
    private String ico;
    private String obchodniJmeno;
    private AresSidloDTO sidlo;
    private String pravniForma;
    private String financniUrad;
    private String datumVzniku;
    private String datumAktualizace;
    private String dic;
    private String icoId;
    private AresAdresaDorucovaciDTO adresaDorucovaci;

}
