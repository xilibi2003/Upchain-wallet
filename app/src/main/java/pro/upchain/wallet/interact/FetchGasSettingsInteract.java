package pro.upchain.wallet.interact;


import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.repository.SharedPreferenceRepository;

public class FetchGasSettingsInteract {
    private final SharedPreferenceRepository repository;

    public FetchGasSettingsInteract(SharedPreferenceRepository repository) {
        this.repository = repository;
    }

    public GasSettings fetch(boolean forTokenTransfer) {
        return repository.getGasSettings(forTokenTransfer);
    }

}
