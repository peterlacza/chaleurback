package hu.elte.chaleur.service;

import hu.elte.chaleur.datastore.EFSAEnergy;
import hu.elte.chaleur.datastore.EFSAMineralVitamin;
import hu.elte.chaleur.model.ReferenceValue;
import hu.elte.chaleur.model.User;
import hu.elte.chaleur.repository.EFSAEnergyRepository;
import hu.elte.chaleur.repository.EFSAMineralVitaminRepository;
import hu.elte.chaleur.repository.ReferenceValueRepository;
import hu.elte.chaleur.repository.UnitRepository;
import hu.elte.chaleur.security.AuthService;
import hu.elte.chaleur.specification.EFSASpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferenceService {
    private final EFSAMineralVitaminRepository efsaMineralVitaminRepository;
    private final EFSAEnergyRepository efsaEnergyRepository;
    private final UnitRepository unitRepository;
    private final ReferenceValueRepository referenceValueRepository;
    private final AuthService authService;

    public List<ReferenceValue> setNutrientsRef(User user){
        List<EFSAMineralVitamin> efsaRefMV = efsaMineralVitaminRepository.findAll(Specification.where(
                EFSASpecification.findByAgeAndGender(user.getAge(), user.getGender())
        ));

        List<EFSAEnergy> efsaRefE = efsaEnergyRepository.findAll(Specification.where((
                EFSASpecification.findByAgeAndGenderAndPal(user.getAge(), user.getGender(), user.getActivity())
        )));

        Float energyReference = efsaRefE.get(0).getAr();

        List<ReferenceValue> userReferences = new ArrayList<>();
        for(EFSAMineralVitamin actNutrient : efsaRefMV){
            ReferenceValue userReference = new ReferenceValue();
            userReference.setUser(authService.getActUser());
            userReference.setUnit(actNutrient.getUnit());
            userReference.setNutrientCode(actNutrient.getCode());

            Double dailyRec = null;
            Double dailyMax = null;
            if (actNutrient.getPri() != null) {
                dailyRec = actNutrient.getPri();
            } else if (actNutrient.getAr() != null) {
                dailyRec = actNutrient.getAr();
            } else if (actNutrient.getAi() != null) {
                dailyRec = actNutrient.getAi();
            } else if(actNutrient.getAdequate() != null){
                dailyRec = actNutrient.getAdequate();
            }
            if (actNutrient.getUl() != null) {
                dailyMax = actNutrient.getUl();
            } else {
                dailyMax = dailyRec * 10;
            }

            if(actNutrient.getUnit().getCode().equals("MGPERKJ")){
                dailyRec *= energyReference;
                dailyMax *= energyReference;
                userReference.setUnit(unitRepository.findByCode("MG"));
            }

            userReference.setDailyMaximum(dailyMax);
            userReference.setDailyRecommend(dailyRec);
            referenceValueRepository.save(userReference);
            userReferences.add(userReference);
        }

        return userReferences;
    }

    public List<ReferenceValue> getReference(){
        return referenceValueRepository.findAllByUser(authService.getActUser());
    }

}
