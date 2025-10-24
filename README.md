# 📞 ft_hangouts

## 📝 Descrizione del Progetto

**ft_hangouts** è un progetto di sviluppo mobile incentrato sulla creazione di un'applicazione completa per la **gestione dei contatti** e l'invio/ricezione di **messaggi di testo (SMS)**, destinata al sistema operativo Android.

L'obiettivo principale del progetto è duplice:
1.  Familiarizzare con le basi dello sviluppo mobile.
2.  Acquisire esperienza pratica con il **Ciclo di Vita dell'Activity** di Android e l'interazione con l'Android SDK.

L'applicazione funge da gestore di contatti indipendente, utilizzando il proprio database SQLite per l'archiviazione.

## 🛠️ Stack Tecnologico e Vincoli

| Aspetto | Dettaglio |
| :--- | :--- |
| **Piattaforma** | Android |
| **Linguaggio** | Kotlin (Scelta consigliata per lo sviluppo nativo moderno) |
| **Persistenza Dati** | Database **SQLite** custom per contatti e messaggi |
| **SDK** | Android SDK |
| **IDE Consigliato** | Android Studio |
| **Vincolo Chiave** | **È vietato l'uso di qualsiasi libreria esterna** (incluso per il design della UI). Lo sviluppo deve essere nativo. |

## ✨ Funzionalità Obbligatorie (Mandatory Part)

Il progetto è considerato completo solo se tutte le seguenti funzionalità sono implementate e funzionanti correttamente.

### Gestione Contatti e UI
* Creazione, modifica e cancellazione di un contatto (ogni contatto richiede almeno 5 campi).
* I contatti sono memorizzati permanentemente nel database SQLite proprietario.
* **Homepage:** Visualizzazione di una lista che riassume ogni contatto.
* **Dettagli:** Possibilità di toccare un contatto per visualizzare i dettagli completi.
* **Temi:** Un menu per cambiare il colore dell'intestazione (header) dell'app.
* **Icona:** L'icona dell'app deve essere il logo di 42.

### Localizzazione e Layout
* Supporto per **due lingue** diverse (una come predefinita).
* La lingua dell'app si adatta automaticamente al cambio di lingua del sistema operativo.
* L'app deve supportare sia la modalità **landscape** che **portrait**.

### Messaggistica e Activity Lifecycle
* Invio e ricezione di messaggi di testo (SMS) da/verso i contatti salvati.
* Visualizzazione di una cronologia di conversazione chiara, che identifichi mittente e destinatario.
* **Activity Lifecycle Tracking:** Quando l'app va in background, viene salvato un timestamp. Al ritorno in foreground, un **Toast** mostra l'orario dell'ultimo background.

## ⭐ Funzionalità Bonus (Bonus Part)

Le seguenti aggiunte saranno valutate solo se la Parte Obbligatoria è stata completata in modo impeccabile ("Perfect").

* Aggiungere una foto profilo a ciascun contatto.
* Creare automaticamente un nuovo contatto (con il numero come nome) se si riceve un messaggio da un numero sconosciuto.
* Migliorare l'interfaccia utente aderendo ai principi di **Material Design**.
* Aggiungere la funzionalità per avviare una chiamata al contatto direttamente dall'app.