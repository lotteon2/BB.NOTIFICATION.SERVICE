package kr.bb.notification.domain.notification.infrastructure.message;

import bloomingblooms.domain.notification.NotificationData;
import bloomingblooms.domain.notification.Role;
import bloomingblooms.domain.notification.delivery.DeliveryNotification;
import bloomingblooms.domain.notification.newcomer.NewcomerNotification;
import bloomingblooms.domain.notification.neworder.NewOrderNotification;
import bloomingblooms.domain.notification.question.QuestionRegister;
import bloomingblooms.domain.resale.ResaleNotificationList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import kr.bb.notification.domain.notification.helper.NotificationActionHelper;
import kr.bb.notification.domain.notification.infrastructure.dto.SettlementNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSQSListener {
  private final ObjectMapper objectMapper;
  private final NotificationActionHelper notificationFacadeHandler;

  /**
   * 상품 재입고 알림
   *
   * @param message
   * @param headers
   * @param ack
   * @throws JsonProcessingException
   */
  @SqsListener(
      value = "${cloud.aws.sqs.product-resale-notification-queue.name}",
      deletionPolicy = SqsMessageDeletionPolicy.NEVER)
  public void consumeProductResaleNotificationCheckQueue(
      @Payload String message, @Headers Map<String, String> headers, Acknowledgment ack)
      throws JsonProcessingException {
    NotificationData<ResaleNotificationList> restoreNotification =
        objectMapper.readValue(
            message,
            objectMapper
                .getTypeFactory()
                .constructParametricType(NotificationData.class, ResaleNotificationList.class));
    restoreNotification.getPublishInformation().setRole(Role.CUSTOMER);
    // call facade
    notificationFacadeHandler.publishResaleNotification(restoreNotification);
    ack.acknowledge();
  }

  /**
   * 문의 등록 알림
   *
   * @param message
   * @param headers
   * @param ack
   * @throws JsonProcessingException
   */
  @SqsListener(
      value = "${cloud.aws.sqs.question-register-notification-queue.name}",
      deletionPolicy = SqsMessageDeletionPolicy.NEVER)
  public void consumeQuestionRegisterNotificationQueue(
      @Payload String message, @Headers Map<String, String> headers, Acknowledgment ack)
      throws JsonProcessingException {
    NotificationData<QuestionRegister> questionRegisterNotification =
        objectMapper.readValue(
            message,
            objectMapper
                .getTypeFactory()
                .constructParametricType(NotificationData.class, QuestionRegister.class));
    questionRegisterNotification.getPublishInformation().setRole(Role.MANAGER);
    // call facade
    notificationFacadeHandler.publishQuestionRegisterNotification(questionRegisterNotification);
    ack.acknowledge();
  }

  /**
   * 신규 주문 알림 배송 | 픽업 | 구독
   *
   * @param message
   * @param headers
   * @param ack
   * @throws JsonProcessingException
   */
  @SqsListener(
      value = "${cloud.aws.sqs.new-order-queue.name}",
      deletionPolicy = SqsMessageDeletionPolicy.NEVER)
  public void consumeNewOrderNotificationQueue(
      @Payload String message, @Headers Map<String, String> headers, Acknowledgment ack)
      throws JsonProcessingException {
    // TODO: 주문 완료 후 타입 정의해서 보내주기 kind
    NotificationData<NewOrderNotification> newOrderNotification =
        objectMapper.readValue(
            message,
            objectMapper
                .getTypeFactory()
                .constructParametricType(NotificationData.class, NewOrderNotification.class));
    newOrderNotification.getPublishInformation().setRole(Role.MANAGER);
    // call facade
    notificationFacadeHandler.publishNewOrderNotification(newOrderNotification);

    ack.acknowledge();
  }

  /**
   * 신규 회원 가입 심사 알림
   *
   * @param message
   * @param headers
   * @param ack
   * @throws JsonProcessingException
   */
  @SqsListener(
      value = "${cloud.aws.sqs.newcomer-queue.name}",
      deletionPolicy = SqsMessageDeletionPolicy.NEVER)
  public void consumeNewcomerQueue(
      @Payload String message, @Headers Map<String, String> headers, Acknowledgment ack)
      throws JsonProcessingException {
    NotificationData<Void> newcomerNotification =
        objectMapper.readValue(
            message,
            objectMapper
                .getTypeFactory()
                .constructParametricType(NotificationData.class, NewcomerNotification.class));
    newcomerNotification.getPublishInformation().setRole(Role.ADMIN);
    // call facade
    notificationFacadeHandler.publishNewComerNotification(newcomerNotification);
    ack.acknowledge();
  }

  /**
   * 배송 시작 알림
   *
   * @param message
   * @param headers
   * @param ack
   * @throws JsonProcessingException
   */
  @SqsListener(
      value = "${cloud.aws.sqs.delivery-start-notification-queue.name}",
      deletionPolicy = SqsMessageDeletionPolicy.NEVER)
  public void consumeDeliveryStartNotificationQueue(
      @Payload String message, @Headers Map<String, String> headers, Acknowledgment ack)
      throws JsonProcessingException {
    NotificationData<DeliveryNotification> deliveryNotification =
        objectMapper.readValue(
            message,
            objectMapper
                .getTypeFactory()
                .constructParametricType(NotificationData.class, DeliveryNotification.class));
    deliveryNotification.getPublishInformation().setRole(Role.CUSTOMER);
    // call facade
    notificationFacadeHandler.publishDeliveryStartNotification(deliveryNotification);
    ack.acknowledge();
  }

  @SqsListener(
      value = "${cloud.aws.sqs.settlement-notification-queue.name}",
      deletionPolicy = SqsMessageDeletionPolicy.NEVER)
  public void consumeSettlementNotificationQueue(
      @Payload String message, @Headers Map<String, String> headers, Acknowledgment ack)
      throws JsonProcessingException {
    NotificationData<SettlementNotification> settlementNotification =
        objectMapper.readValue(
            message,
            objectMapper
                .getTypeFactory()
                .constructParametricType(NotificationData.class, SettlementNotification.class));
    settlementNotification.getPublishInformation().setRole(Role.MANAGER);
    // call facade
    notificationFacadeHandler.publishSettlementNotification(settlementNotification);
    ack.acknowledge();
  }
}
