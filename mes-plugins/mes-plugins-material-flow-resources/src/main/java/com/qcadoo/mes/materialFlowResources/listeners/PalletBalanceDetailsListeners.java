package com.qcadoo.mes.materialFlowResources.listeners;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.qcadoo.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.qcadoo.mes.materialFlowResources.constants.PalletBalanceFields;
import com.qcadoo.mes.materialFlowResources.palletBalance.PalletBalanceXlsService;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.file.FileService;
import com.qcadoo.report.api.ReportService;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;

@Service
public class PalletBalanceDetailsListeners {

    @Autowired
    private FileService fileService;

    @Autowired
    private PalletBalanceXlsService palletBalanceXlsService;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    @Autowired
    private ReportService reportService;

    public void printPalletBalance(final ViewDefinitionState viewDefinitionState, final ComponentState state, final String[] args) {
        reportService.printGeneratedReport(viewDefinitionState, state, new String[] { args[0],
                MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER, MaterialFlowResourcesConstants.MODEL_PALLET_BALANCE });
    }

    @Transactional
    public void generatePalletBalance(final ViewDefinitionState viewDefinitionState, final ComponentState state,
            final String[] args) {
        state.performEvent(viewDefinitionState, "save", new String[0]);

        if (!state.isHasError()) {
            Entity report = getReportFromDB((Long) state.getFieldValue());

            if (report == null) {
                state.addMessage("qcadooView.message.entityNotFound", ComponentState.MessageType.FAILURE);
                return;
            } else if (StringUtils.hasText(report.getStringField(PalletBalanceFields.FILE_NAME))) {
                state.addMessage("materialFlowResource.palletBalance.report.error.documentsWasGenerated",
                        ComponentState.MessageType.FAILURE);
                return;
            }

            if (!report.getBooleanField(PalletBalanceFields.GENERATED)) {
                fillReportValues(report);
            }
            report = getReportFromDB((Long) state.getFieldValue());

            try {
                generateReport(report, state.getLocale());

                state.performEvent(viewDefinitionState, "reset", new String[0]);

                state.addMessage("materialFlowResource.palletBalance.report.generatedMessage", ComponentState.MessageType.SUCCESS);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } catch (DocumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    private void fillReportValues(final Entity report) {
        report.setField(PalletBalanceFields.GENERATED, true);
        report.setField(PalletBalanceFields.DATE_TO, new Date());
        report.getDataDefinition().save(report);
    }

    private void generateReport(final Entity palletBalance, final Locale locale) throws IOException, DocumentException {

        Entity palletBalanceWithFilename = fileService.updateReportFileName(palletBalance, PalletBalanceFields.DATE_TO,
                "materialFlowResource.palletBalance.report.fileName");
        try {
            palletBalanceXlsService.generateDocument(palletBalanceWithFilename, locale, PageSize.A3);

        } catch (IOException e) {
            throw new IllegalStateException("Problem with saving goodFood report");
        }
    }

    private Entity getReportFromDB(final Long entityId) {
        return dataDefinitionService.get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER,
                MaterialFlowResourcesConstants.MODEL_PALLET_BALANCE).get(entityId);
    }
}
