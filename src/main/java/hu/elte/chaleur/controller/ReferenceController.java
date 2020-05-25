package hu.elte.chaleur.controller;

import hu.elte.chaleur.efsa.EFSAEnergy;
import hu.elte.chaleur.efsa.EFSAMineralVitamin;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reference")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReferenceController {
    private final EFSAEnergyRepository efsaEnergyRepository;
    private final EFSAMineralVitaminRepository efsaMineralVitaminRepository;
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

        //Van speciális állapot?
        //Ha igen, az energynél PLUSZ-olunk, a Mineral&Vitamins-nál felülírjuk
        //De első körben spec állapot nélkül fut le
        //TODO: most még nincs megírva a speciális állapotokra!
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

    @GetMapping(value = "/getReference")
    public List<ReferenceValue> getRef(){
        return referenceValueRepository.findAllByUser(authService.getActUser());
    }
}
