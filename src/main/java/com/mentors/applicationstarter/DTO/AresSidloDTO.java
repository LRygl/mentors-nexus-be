package com.mentors.applicationstarter.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AresSidloDTO {
    private String kodStatu;
    private String nazevStatu;
    private Integer kodKraje;
    private String nazevKraje;
    private Integer kodObce;
    private String nazevObce;
    private Integer kodSpravnihoObvodu;
    private String nazevSpravnihoObvodu;
    private Integer kodMestskehoObvodu;
    private String nazevMestskehoObvodu;
    private Integer kodMestskeCastiObvodu;
    private Integer kodUlice;
    private String nazevUlice;
    private Integer cisloDomovni;
    private Integer kodCastiObce;
    private Integer cisloOrientacni;
    private String nazevCastiObce;
    private Long kodAdresnihoMista;
    private Integer psc;
    private String textovaAdresa;
    private Boolean standardizaceAdresy;
    private Integer typCisloDomovni;
}
