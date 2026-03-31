package enums;

//2+1 otobuslerde, numaralandirma yontemi ikiye ayrilir
//1 - Atlamasiz - 1   2 3 / 4   5 6 / 7   8 9 gibi
//2 - Atlamali  - 1   3 4 / 5   7 8 / 9  11 12 gibi
public enum NumberingStyle {
    SKIPPING, //Atlamali
    CONTINUOUS; //Atlamasiz
}